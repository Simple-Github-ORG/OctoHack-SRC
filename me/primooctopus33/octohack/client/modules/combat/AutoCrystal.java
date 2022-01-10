package me.primooctopus33.octohack.client.modules.combat;

import com.mojang.authlib.GameProfile;
import io.netty.util.internal.ConcurrentSet;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.modules.client.ClickGui;
import me.primooctopus33.octohack.client.setting.Bind;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.event.events.ClientEvent;
import me.primooctopus33.octohack.event.events.PacketEvent;
import me.primooctopus33.octohack.event.events.Render3DEvent;
import me.primooctopus33.octohack.event.events.UpdateWalkingPlayerEvent;
import me.primooctopus33.octohack.util.BlockUtil;
import me.primooctopus33.octohack.util.DamageUtil;
import me.primooctopus33.octohack.util.EntityUtil;
import me.primooctopus33.octohack.util.InventoryUtil;
import me.primooctopus33.octohack.util.MathUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemEndCrystal;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class AutoCrystal
extends Module {
    public static EntityPlayer target = null;
    public static Set<BlockPos> lowDmgPos = new ConcurrentSet();
    public static Set<BlockPos> placedPos = new HashSet<BlockPos>();
    public static Set<BlockPos> brokenPos = new HashSet<BlockPos>();
    private static AutoCrystal instance;
    public final Timer threadTimer = new Timer();
    private final Setting<Settings> setting = this.register(new Setting<Settings>("Settings", Settings.PLACE));
    public final Setting<Boolean> attackOppositeHand = this.register(new Setting<Object>("Opposite Hand", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    public final Setting<Boolean> removeAfterAttack = this.register(new Setting<Object>("Attack Remove", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    public final Setting<Boolean> antiBlock = this.register(new Setting<Object>("Anti Feet Place", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
    private final Setting<Integer> switchCooldown = this.register(new Setting<Object>("Cooldown", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> this.setting.getValue() == Settings.MISC));
    private final Setting<Integer> eventMode = this.register(new Setting<Object>("Updates", Integer.valueOf(3), Integer.valueOf(1), Integer.valueOf(3), v -> this.setting.getValue() == Settings.DEV));
    private final Timer switchTimer = new Timer();
    private final Timer manualTimer = new Timer();
    private final Timer breakTimer = new Timer();
    private final Timer placeTimer = new Timer();
    private final Timer syncTimer = new Timer();
    private final Timer predictTimer = new Timer();
    private final Timer renderTimer = new Timer();
    private final AtomicBoolean shouldInterrupt = new AtomicBoolean(false);
    private final Timer syncroTimer = new Timer();
    private final Map<EntityPlayer, Timer> totemPops = new ConcurrentHashMap<EntityPlayer, Timer>();
    private final Queue<CPacketUseEntity> packetUseEntities = new LinkedList<CPacketUseEntity>();
    private final AtomicBoolean threadOngoing = new AtomicBoolean(false);
    public Setting<Raytrace> raytrace = this.register(new Setting<Object>("Raytrace", (Object)Raytrace.NONE, v -> this.setting.getValue() == Settings.MISC));
    public Setting<Boolean> place = this.register(new Setting<Object>("Place", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE));
    public Setting<Integer> placeDelay = this.register(new Setting<Object>("Place Delay", Integer.valueOf(25), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> placeRange = this.register(new Setting<Object>("Place Range", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> packetPlace = this.register(new Setting<Object>("Packet Place", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> minDamage = this.register(new Setting<Object>("Min Damage", Float.valueOf(7.0f), Float.valueOf(0.1f), Float.valueOf(20.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> maxSelfPlace = this.register(new Setting<Object>("Max Self Place", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Integer> wasteAmount = this.register(new Setting<Object>("Waste Amount", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> wasteMinDmgCount = this.register(new Setting<Object>("Count Min Dmg", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> facePlace = this.register(new Setting<Object>("Face Place", Float.valueOf(14.4f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Float> placetrace = this.register(new Setting<Object>("Place trace", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.BREAK));
    public Setting<Boolean> antiSurround = this.register(new Setting<Object>("Anti Surround", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> limitFacePlace = this.register(new Setting<Object>("Limit Face Place", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> oneDot15 = this.register(new Setting<Object>("1.15", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> holePlace = this.register(new Setting<Object>("Hole Place", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Boolean> doublePop = this.register(new Setting<Object>("Anti Totem", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false));
    public Setting<Double> popHealth = this.register(new Setting<Object>("Pop Health", Double.valueOf(1.0), Double.valueOf(0.0), Double.valueOf(3.0), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.doublePop.getValue() != false));
    public Setting<Float> popDamage = this.register(new Setting<Object>("Pop Damage", Float.valueOf(4.0f), Float.valueOf(0.0f), Float.valueOf(6.0f), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.doublePop.getValue() != false));
    public Setting<Integer> popTime = this.register(new Setting<Object>("Pop Time", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(1000), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.doublePop.getValue() != false));
    public Setting<Boolean> explode = this.register(new Setting<Object>("Break", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK));
    public Setting<AntiWeaknessMode> antiWeakness;
    public Setting<Switch> switchMode = this.register(new Setting<Object>("Attack", (Object)Switch.BREAKSLOT, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
    public Setting<Integer> breakDelay;
    public Setting<Float> breakRange;
    public Setting<Integer> packets;
    public Setting<Float> maxSelfBreak;
    public Setting<Float> breaktrace;
    public Setting<Boolean> manual;
    public Setting<Boolean> manualMinDmg;
    public Setting<Integer> manualBreak;
    public Setting<Boolean> sync;
    public Setting<Boolean> instant;
    public Setting<PredictTimer> instantTimer;
    public Setting<Boolean> resetBreakTimer;
    public Setting<Integer> predictDelay;
    public Setting<Boolean> predictCalc;
    public Setting<Boolean> superSafe;
    public Setting<Boolean> antiCommit;
    public Setting<Boolean> render;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    public Setting<Boolean> colorSync;
    public Setting<Boolean> box;
    private final Setting<Integer> boxAlpha;
    public Setting<Boolean> outline;
    private final Setting<Float> lineWidth;
    public Setting<Boolean> text;
    public Setting<Boolean> customOutline;
    private final Setting<Integer> cRed;
    private final Setting<Integer> cGreen;
    private final Setting<Integer> cBlue;
    private final Setting<Integer> cAlpha;
    public Setting<Boolean> holdFacePlace;
    public Setting<Boolean> holdFaceBreak;
    public Setting<Boolean> slowFaceBreak;
    public Setting<Boolean> actualSlowBreak;
    public Setting<Integer> facePlaceSpeed;
    public Setting<Boolean> antiNaked;
    public Setting<Timing> timing;
    public Setting<Float> range;
    public Setting<Target> targetMode;
    public Setting<Integer> minArmor;
    public Setting<AutoSwitch> autoSwitch;
    public Setting<Bind> switchBind;
    public Setting<Boolean> offhandSwitch;
    public Setting<Boolean> switchBack;
    public Setting<Boolean> lethalSwitch;
    public Setting<Boolean> mineSwitch;
    public Setting<Rotate> rotate;
    public Setting<Boolean> suicide;
    public Setting<Boolean> webAttack;
    public Setting<Boolean> fullCalc;
    public Setting<Boolean> sound;
    public Setting<Float> soundRange;
    public Setting<Float> soundPlayer;
    public Setting<Boolean> soundConfirm;
    public Setting<Boolean> extraSelfCalc;
    public Setting<AntiFriendPop> antiFriendPop;
    public Setting<Boolean> noCount;
    public Setting<Boolean> calcEvenIfNoDamage;
    public Setting<Boolean> predictFriendDmg;
    public Setting<Float> minMinDmg;
    public Setting<Boolean> breakSwing;
    public Setting<Boolean> placeSwing;
    public Setting<Boolean> exactHand;
    public Setting<Boolean> justRender;
    public Setting<Boolean> fakeSwing;
    public Setting<Logic> logic;
    public Setting<Boolean> terrainIgnore;
    public Setting<DamageSync> damageSync;
    public Setting<Integer> damageSyncTime;
    public Setting<Float> dropOff;
    public Setting<Integer> confirm;
    public Setting<Boolean> syncedFeetPlace;
    public Setting<Boolean> fullSync;
    public Setting<Boolean> syncCount;
    public Setting<Boolean> hyperSync;
    public Setting<Boolean> gigaSync;
    public Setting<Boolean> syncySync;
    public Setting<Boolean> enormousSync;
    public Setting<Boolean> holySync;
    public Setting<Boolean> rotateFirst;
    public Setting<ThreadMode> threadMode;
    public Setting<Integer> threadDelay;
    public Setting<Boolean> syncThreadBool;
    public Setting<Integer> syncThreads;
    public Setting<Boolean> predictPos;
    public Setting<Boolean> renderExtrapolation;
    public Setting<Integer> predictTicks;
    public Setting<Integer> rotations;
    public Setting<Boolean> predictRotate;
    public Setting<Float> predictOffset;
    public Setting<Boolean> doublePopOnDamage;
    public boolean rotating = false;
    private Queue<Entity> attackList;
    private Map<Entity, Float> crystalMap;
    private Entity efficientTarget = null;
    private double currentDamage = 0.0;
    private double renderDamage = 0.0;
    private double lastDamage = 0.0;
    private boolean didRotation = false;
    private boolean switching = false;
    private BlockPos placePos = null;
    private BlockPos renderPos = null;
    private boolean mainHand = false;
    private boolean offHand = false;
    private int crystalCount = 0;
    private int minDmgCount = 0;
    private int lastSlot = -1;
    private float yaw = 0.0f;
    private float pitch = 0.0f;
    private BlockPos webPos = null;
    private BlockPos lastPos = null;
    private boolean posConfirmed = false;
    private boolean foundDoublePop = false;
    private int rotationPacketsSpoofed = 0;
    private ScheduledExecutorService executor;
    private Thread thread;
    private EntityPlayer currentSyncTarget;
    private BlockPos syncedPlayerPos;
    private BlockPos syncedCrystalPos;
    private PlaceInfo placeInfo;
    private boolean addTolowDmg;
    private boolean silentSwitch;

    public AutoCrystal() {
        super("AutoCrystal", "Automatically places and breaks End Crystals to kill your opponent", Module.Category.COMBAT, true, false, false);
        this.antiWeakness = this.register(new Setting<Object>("Anti Weakness", (Object)AntiWeaknessMode.Silent, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
        this.breakDelay = this.register(new Setting<Object>("Break Delay", Integer.valueOf(50), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
        this.breakRange = this.register(new Setting<Object>("Break Range", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
        this.packets = this.register(new Setting<Object>("Packets", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(6), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
        this.maxSelfBreak = this.register(new Setting<Object>("Max Self Break", Float.valueOf(10.0f), Float.valueOf(0.1f), Float.valueOf(36.0f), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false));
        this.breaktrace = this.register(new Setting<Object>("Break trace", Float.valueOf(4.5f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.raytrace.getValue() != Raytrace.NONE && this.raytrace.getValue() != Raytrace.PLACE));
        this.manual = this.register(new Setting<Object>("Manual", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK));
        this.manualMinDmg = this.register(new Setting<Object>("Man Min Dmg", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue() != false));
        this.manualBreak = this.register(new Setting<Object>("Manual Delay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.BREAK && this.manual.getValue() != false));
        this.sync = this.register(new Setting<Object>("Sync", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && (this.explode.getValue() != false || this.manual.getValue() != false)));
        this.instant = this.register(new Setting<Object>("Predict", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false));
        this.instantTimer = this.register(new Setting<Object>("Predict Timer", (Object)PredictTimer.NONE, v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
        this.resetBreakTimer = this.register(new Setting<Object>("Reset Break Timer", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
        this.predictDelay = this.register(new Setting<Object>("Predict Delay", Integer.valueOf(12), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false && this.instantTimer.getValue() == PredictTimer.PREDICT));
        this.predictCalc = this.register(new Setting<Object>("Predict Calc", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
        this.superSafe = this.register(new Setting<Object>("Super Safe", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
        this.antiCommit = this.register(new Setting<Object>("Anti Over Commit", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.BREAK && this.explode.getValue() != false && this.place.getValue() != false && this.instant.getValue() != false));
        this.render = this.register(new Setting<Object>("Render", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER));
        this.red = this.register(new Setting<Object>("Red", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
        this.green = this.register(new Setting<Object>("Green", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
        this.blue = this.register(new Setting<Object>("Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
        this.alpha = this.register(new Setting<Object>("Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
        this.colorSync = this.register(new Setting<Object>("CSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER));
        this.box = this.register(new Setting<Object>("Box", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
        this.boxAlpha = this.register(new Setting<Object>("Box Alpha", Integer.valueOf(125), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.box.getValue() != false));
        this.outline = this.register(new Setting<Object>("Outline", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
        this.lineWidth = this.register(new Setting<Object>("LineWidth", Float.valueOf(1.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
        this.text = this.register(new Setting<Object>("Text", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false));
        this.customOutline = this.register(new Setting<Object>("Custom Line", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.outline.getValue() != false));
        this.cRed = this.register(new Setting<Object>("Outline Red", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.customOutline.getValue() != false && this.outline.getValue() != false));
        this.cGreen = this.register(new Setting<Object>("Outline Green", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.customOutline.getValue() != false && this.outline.getValue() != false));
        this.cBlue = this.register(new Setting<Object>("Outline Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.customOutline.getValue() != false && this.outline.getValue() != false));
        this.cAlpha = this.register(new Setting<Object>("Outline Alpha", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.setting.getValue() == Settings.RENDER && this.render.getValue() != false && this.customOutline.getValue() != false && this.outline.getValue() != false));
        this.holdFacePlace = this.register(new Setting<Object>("Hold Face Place", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
        this.holdFaceBreak = this.register(new Setting<Object>("Hold Slow Break", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && this.holdFacePlace.getValue() != false));
        this.slowFaceBreak = this.register(new Setting<Object>("Slow Face Break", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
        this.actualSlowBreak = this.register(new Setting<Object>("Actually Slow", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
        this.facePlaceSpeed = this.register(new Setting<Object>("Face Speed", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.MISC));
        this.antiNaked = this.register(new Setting<Object>("Anti Naked", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
        this.timing = this.register(new Setting<Object>("Timing", (Object)Timing.Sequential, v -> this.setting.getValue() == Settings.MISC));
        this.range = this.register(new Setting<Object>("Range", Float.valueOf(12.0f), Float.valueOf(0.1f), Float.valueOf(20.0f), v -> this.setting.getValue() == Settings.MISC));
        this.targetMode = this.register(new Setting<Object>("Target", (Object)Target.CLOSEST, v -> this.setting.getValue() == Settings.MISC));
        this.minArmor = this.register(new Setting<Object>("Min Armor", Integer.valueOf(5), Integer.valueOf(0), Integer.valueOf(125), v -> this.setting.getValue() == Settings.MISC));
        this.autoSwitch = this.register(new Setting<Object>("Switch", (Object)AutoSwitch.SILENT, v -> this.setting.getValue() == Settings.MISC));
        this.switchBind = this.register(new Setting<Object>("Switch Bind", new Bind(-1), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() == AutoSwitch.TOGGLE));
        this.offhandSwitch = this.register(new Setting<Object>("Offhand", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.switchBack = this.register(new Setting<Object>("Switch back", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE && this.offhandSwitch.getValue() != false));
        this.lethalSwitch = this.register(new Setting<Object>("Lethal Switch", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.mineSwitch = this.register(new Setting<Object>("Mine Switch", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC && this.autoSwitch.getValue() != AutoSwitch.NONE));
        this.rotate = this.register(new Setting<Object>("Rotate", (Object)Rotate.OFF, v -> this.setting.getValue() == Settings.MISC));
        this.suicide = this.register(new Setting<Object>("Suicide", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
        this.terrainIgnore = this.register(new Setting<Object>("Terrain Ignore", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC));
        this.webAttack = this.register(new Setting<Object>("Web Attack", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC && this.targetMode.getValue() != Target.DAMAGE));
        this.fullCalc = this.register(new Setting<Object>("Extra Calc", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
        this.sound = this.register(new Setting<Object>("Sound", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC));
        this.soundRange = this.register(new Setting<Object>("Sound Range", Float.valueOf(12.0f), Float.valueOf(0.0f), Float.valueOf(12.0f), v -> this.setting.getValue() == Settings.MISC));
        this.soundPlayer = this.register(new Setting<Object>("Sound Player", Float.valueOf(6.0f), Float.valueOf(0.0f), Float.valueOf(12.0f), v -> this.setting.getValue() == Settings.MISC));
        this.soundConfirm = this.register(new Setting<Object>("Sound Confirm", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.MISC));
        this.extraSelfCalc = this.register(new Setting<Object>("Min Self Dmg", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC));
        this.antiFriendPop = this.register(new Setting<Object>("Friend Pop", (Object)AntiFriendPop.NONE, v -> this.setting.getValue() == Settings.MISC));
        this.noCount = this.register(new Setting<Object>("Anti Count", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK)));
        this.calcEvenIfNoDamage = this.register(new Setting<Object>("Big Friend Calc", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.targetMode.getValue() != Target.DAMAGE));
        this.predictFriendDmg = this.register(new Setting<Object>("Predict Friend", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.MISC && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.BREAK) && this.instant.getValue() != false));
        this.minMinDmg = this.register(new Setting<Object>("Min Min Dmg", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(3.0f), v -> this.setting.getValue() == Settings.DEV && this.place.getValue() != false));
        this.breakSwing = this.register(new Setting<Object>("BreakSwing", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.DEV));
        this.placeSwing = this.register(new Setting<Object>("PlaceSwing", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
        this.exactHand = this.register(new Setting<Object>("ExactHand", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.placeSwing.getValue() != false));
        this.justRender = this.register(new Setting<Object>("JustRender", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
        this.fakeSwing = this.register(new Setting<Object>("FakeSwing", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.justRender.getValue() != false));
        this.logic = this.register(new Setting<Object>("Logic", (Object)Logic.BREAKPLACE, v -> this.setting.getValue() == Settings.DEV));
        this.damageSync = this.register(new Setting<Object>("DamageSync", (Object)DamageSync.NONE, v -> this.setting.getValue() == Settings.DEV));
        this.damageSyncTime = this.register(new Setting<Object>("SyncDelay", Integer.valueOf(500), Integer.valueOf(0), Integer.valueOf(500), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.dropOff = this.register(new Setting<Object>("DropOff", Float.valueOf(5.0f), Float.valueOf(0.0f), Float.valueOf(10.0f), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() == DamageSync.BREAK));
        this.confirm = this.register(new Setting<Object>("Confirm", Integer.valueOf(250), Integer.valueOf(0), Integer.valueOf(1000), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.syncedFeetPlace = this.register(new Setting<Object>("FeetSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE));
        this.fullSync = this.register(new Setting<Object>("FullSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
        this.syncCount = this.register(new Setting<Object>("SyncCount", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
        this.hyperSync = this.register(new Setting<Object>("HyperSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
        this.gigaSync = this.register(new Setting<Object>("GigaSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
        this.syncySync = this.register(new Setting<Object>("SyncySync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
        this.enormousSync = this.register(new Setting<Object>("EnormousSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
        this.holySync = this.register(new Setting<Object>("UnbelievableSync", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.damageSync.getValue() != DamageSync.NONE && this.syncedFeetPlace.getValue() != false));
        this.rotateFirst = this.register(new Setting<Object>("FirstRotation", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.rotate.getValue() != Rotate.OFF && this.eventMode.getValue() == 2));
        this.threadMode = this.register(new Setting<Object>("Thread", (Object)ThreadMode.NONE, v -> this.setting.getValue() == Settings.DEV));
        this.threadDelay = this.register(new Setting<Object>("ThreadDelay", Integer.valueOf(50), Integer.valueOf(1), Integer.valueOf(1000), v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE));
        this.syncThreadBool = this.register(new Setting<Object>("ThreadSync", Boolean.valueOf(true), v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE));
        this.syncThreads = this.register(new Setting<Object>("SyncThreads", Integer.valueOf(1000), Integer.valueOf(1), Integer.valueOf(10000), v -> this.setting.getValue() == Settings.DEV && this.threadMode.getValue() != ThreadMode.NONE && this.syncThreadBool.getValue() != false));
        this.predictPos = this.register(new Setting<Object>("PredictPos", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
        this.renderExtrapolation = this.register(new Setting<Object>("RenderExtrapolation", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV && this.predictPos.getValue() != false));
        this.predictTicks = this.register(new Setting<Object>("ExtrapolationTicks", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(20), v -> this.setting.getValue() == Settings.DEV && this.predictPos.getValue() != false));
        this.rotations = this.register(new Setting<Object>("Spoofs", Integer.valueOf(1), Integer.valueOf(1), Integer.valueOf(20), v -> this.setting.getValue() == Settings.DEV));
        this.predictRotate = this.register(new Setting<Object>("PredictRotate", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.DEV));
        this.predictOffset = this.register(new Setting<Object>("PredictOffset", Float.valueOf(0.0f), Float.valueOf(0.0f), Float.valueOf(4.0f), v -> this.setting.getValue() == Settings.DEV));
        this.doublePopOnDamage = this.register(new Setting<Object>("DamagePop", Boolean.valueOf(false), v -> this.setting.getValue() == Settings.PLACE && this.place.getValue() != false && this.doublePop.getValue() != false && this.targetMode.getValue() == Target.DAMAGE));
        this.attackList = new ConcurrentLinkedQueue<Entity>();
        this.crystalMap = new HashMap<Entity, Float>();
        instance = this;
    }

    public static AutoCrystal getInstance() {
        if (instance == null) {
            instance = new AutoCrystal();
        }
        return instance;
    }

    @Override
    public void onTick() {
        if (this.threadMode.getValue() == ThreadMode.NONE && this.eventMode.getValue() == 3) {
            this.doAutoCrystalRewrite();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateWalkingPlayerEvent event) {
        if (event.getStage() == 1) {
            this.postProcessing();
        }
        if (event.getStage() != 0) {
            return;
        }
        if (this.eventMode.getValue() == 2) {
            this.doAutoCrystalRewrite();
        }
    }

    public void postTick() {
        if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.processMultiThreading();
        }
    }

    @Override
    public void onUpdate() {
        if (this.threadMode.getValue() == ThreadMode.NONE && this.eventMode.getValue() == 1) {
            this.doAutoCrystalRewrite();
        }
    }

    @Override
    public void onToggle() {
        brokenPos.clear();
        placedPos.clear();
        this.totemPops.clear();
        this.rotating = false;
    }

    @Override
    public void onDisable() {
        super.onDisable();
        if (this.thread != null) {
            this.shouldInterrupt.set(true);
        }
        if (this.executor != null) {
            this.executor.shutdown();
        }
    }

    @Override
    public void onEnable() {
        super.onEnable();
        if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.processMultiThreading();
        }
    }

    @Override
    public String getDisplayInfo() {
        if (this.switching) {
            return "\u00ef\u00bf\u00bdaSwitch";
        }
        if (target != null) {
            return target.getName();
        }
        return null;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        CPacketUseEntity packet3;
        if (event.getStage() == 0 && this.rotate.getValue() != Rotate.OFF && this.rotating && this.eventMode.getValue() != 2 && event.getPacket() instanceof CPacketPlayer) {
            CPacketPlayer packet2 = (CPacketPlayer)event.getPacket();
            packet2.yaw = this.yaw;
            packet2.pitch = this.pitch;
            ++this.rotationPacketsSpoofed;
            if (this.rotationPacketsSpoofed >= this.rotations.getValue()) {
                this.rotating = false;
                this.rotationPacketsSpoofed = 0;
            }
        }
        BlockPos pos = null;
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet3 = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet3.getEntityFromWorld(AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            pos = packet3.getEntityFromWorld(AutoCrystal.mc.world).getPosition();
            if (this.removeAfterAttack.getValue().booleanValue()) {
                Objects.requireNonNull(packet3.getEntityFromWorld(AutoCrystal.mc.world)).setDead();
                AutoCrystal.mc.world.removeEntityFromWorld(packet3.entityId);
            }
        }
        if (event.getStage() == 0 && event.getPacket() instanceof CPacketUseEntity && (packet3 = (CPacketUseEntity)event.getPacket()).getAction() == CPacketUseEntity.Action.ATTACK && packet3.getEntityFromWorld(AutoCrystal.mc.world) instanceof EntityEnderCrystal) {
            EntityEnderCrystal crystal = (EntityEnderCrystal)packet3.getEntityFromWorld(AutoCrystal.mc.world);
            if (this.antiBlock.getValue().booleanValue() && EntityUtil.isCrystalAtFeet(crystal, this.range.getValue().floatValue()) && pos != null) {
                this.rotateToPos(pos);
                BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing.getValue(), this.exactHand.getValue(), this.silentSwitch);
            }
        }
    }

    @SubscribeEvent(priority=EventPriority.HIGH, receiveCanceled=true)
    public void onSoundPacket(PacketEvent.Receive event) {
        SPacketSoundEffect packet2;
        if (AutoCrystal.fullNullCheck()) {
            return;
        }
        if (event.getPacket() instanceof SPacketSoundEffect && this.timing.getValue() == Timing.Sequential && (packet2 = (SPacketSoundEffect)event.getPacket()).getCategory() == SoundCategory.BLOCKS && packet2.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
            ArrayList entities = new ArrayList(AutoCrystal.mc.world.loadedEntityList);
            int size = entities.size();
            for (int i = 0; i < size; ++i) {
                Entity entity = (Entity)entities.get(i);
                if (!(entity instanceof EntityEnderCrystal) || !(entity.getDistanceSq(packet2.getX(), packet2.getY(), packet2.getZ()) < 36.0)) continue;
                entity.setDead();
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    @SubscribeEvent(priority=EventPriority.HIGH, receiveCanceled=true)
    public void onPacketReceive(PacketEvent.Receive event) {
        if (AutoCrystal.fullNullCheck()) {
            return;
        }
        if (!this.justRender.getValue().booleanValue() && this.switchTimer.passedMs(this.switchCooldown.getValue().intValue()) && this.explode.getValue().booleanValue() && this.instant.getValue().booleanValue() && event.getPacket() instanceof SPacketSpawnObject && (this.syncedCrystalPos == null || !this.syncedFeetPlace.getValue().booleanValue() || this.damageSync.getValue() == DamageSync.NONE)) {
            BlockPos blockPos;
            SPacketSpawnObject packet2 = (SPacketSpawnObject)event.getPacket();
            if (packet2.getType() != 51) return;
            BlockPos pos = new BlockPos(packet2.getX(), packet2.getY(), packet2.getZ());
            if (!(AutoCrystal.mc.player.getDistanceSq(blockPos) + (double)this.predictOffset.getValue().floatValue() <= MathUtil.square(this.breakRange.getValue().floatValue())) || this.instantTimer.getValue() != PredictTimer.NONE && (this.instantTimer.getValue() != PredictTimer.BREAK || !this.breakTimer.passedMs(this.breakDelay.getValue().intValue())) && (this.instantTimer.getValue() != PredictTimer.PREDICT || !this.predictTimer.passedMs(this.predictDelay.getValue().intValue()))) return;
            if (this.predictSlowBreak(pos.down())) {
                return;
            }
            if (this.predictFriendDmg.getValue().booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.ALL) && this.isRightThread()) {
                for (EntityPlayer friend : AutoCrystal.mc.world.playerEntities) {
                    if (friend == null || AutoCrystal.mc.player.equals(friend) || !(friend.getDistanceSq(pos) <= MathUtil.square(this.range.getValue().floatValue() + this.placeRange.getValue().floatValue())) || !OctoHack.friendManager.isFriend(friend) || (double)DamageUtil.calculateDamage(pos, (Entity)friend) <= (double)EntityUtil.getHealth(friend) + 0.5) continue;
                    return;
                }
            }
            if (placedPos.contains(pos.down())) {
                if (this.isRightThread() && this.superSafe.getValue().booleanValue()) {
                    if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                        float f;
                        float selfDamage = DamageUtil.calculateDamage(pos, (Entity)AutoCrystal.mc.player);
                        if (!((double)f - 0.5 <= (double)EntityUtil.getHealth(AutoCrystal.mc.player)) || !(selfDamage <= this.maxSelfBreak.getValue().floatValue())) return;
                    }
                } else if (this.superSafe.getValue().booleanValue()) {
                    return;
                }
                this.attackCrystalPredict(packet2.getEntityID(), pos);
                return;
            }
            if (!this.predictCalc.getValue().booleanValue() || !this.isRightThread()) return;
            float selfDamage = -1.0f;
            if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                selfDamage = DamageUtil.calculateDamage(pos, (Entity)AutoCrystal.mc.player);
            }
            if (!((double)selfDamage + 0.5 < (double)EntityUtil.getHealth(AutoCrystal.mc.player)) || !(selfDamage <= this.maxSelfBreak.getValue().floatValue())) return;
            for (EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                float f;
                if (!(player.getDistanceSq(pos) <= MathUtil.square(this.range.getValue().floatValue())) || !EntityUtil.isValid(player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue()) || this.antiNaked.getValue().booleanValue() && DamageUtil.isNaked(player)) continue;
                float damage = DamageUtil.calculateDamage(pos, (Entity)player);
                if (f <= selfDamage && (damage <= this.minDamage.getValue().floatValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth(player)) continue;
                if (this.predictRotate.getValue().booleanValue() && this.eventMode.getValue() != 2 && (this.rotate.getValue() == Rotate.BREAK || this.rotate.getValue() == Rotate.ALL)) {
                    this.rotateToPos(pos);
                }
                this.attackCrystalPredict(packet2.getEntityID(), pos);
                return;
            }
            return;
        }
        if (!this.soundConfirm.getValue().booleanValue() && event.getPacket() instanceof SPacketExplosion) {
            SPacketExplosion packet3 = (SPacketExplosion)event.getPacket();
            BlockPos pos2 = new BlockPos(packet3.getX(), packet3.getY(), packet3.getZ()).down();
            this.removePos(pos2);
            return;
        } else if (event.getPacket() instanceof SPacketDestroyEntities) {
            SPacketDestroyEntities packet4 = (SPacketDestroyEntities)event.getPacket();
            for (int id : packet4.getEntityIDs()) {
                Entity entity = AutoCrystal.mc.world.getEntityByID(id);
                if (!(entity instanceof EntityEnderCrystal)) continue;
                brokenPos.remove(new BlockPos(entity.getPositionVector()).down());
                placedPos.remove(new BlockPos(entity.getPositionVector()).down());
            }
            return;
        } else if (event.getPacket() instanceof SPacketEntityStatus) {
            SPacketEntityStatus packet5 = (SPacketEntityStatus)event.getPacket();
            if (packet5.getOpCode() != 35 || !(packet5.getEntity(AutoCrystal.mc.world) instanceof EntityPlayer)) return;
            this.totemPops.put((EntityPlayer)packet5.getEntity(AutoCrystal.mc.world), new Timer().reset());
            return;
        } else {
            SPacketSoundEffect packet6;
            if (!(event.getPacket() instanceof SPacketSoundEffect) || (packet6 = (SPacketSoundEffect)event.getPacket()).getCategory() != SoundCategory.BLOCKS || packet6.getSound() != SoundEvents.ENTITY_GENERIC_EXPLODE) return;
            BlockPos pos = new BlockPos(packet6.getX(), packet6.getY(), packet6.getZ());
            if (this.sound.getValue().booleanValue() || this.threadMode.getValue() == ThreadMode.SOUND) {
                AutoCrystal.removeEntities(packet6, this.soundRange.getValue().floatValue());
            }
            if (this.soundConfirm.getValue().booleanValue()) {
                this.removePos(pos);
            }
            if (this.threadMode.getValue() != ThreadMode.SOUND || !this.isRightThread() || AutoCrystal.mc.player == null || !(AutoCrystal.mc.player.getDistanceSq(pos) < MathUtil.square(this.soundPlayer.getValue().floatValue()))) return;
            this.handlePool(true);
        }
    }

    public static void removeEntities(SPacketSoundEffect packet, float range) {
        BlockPos pos = new BlockPos(packet.getX(), packet.getY(), packet.getZ());
        ArrayList<Entity> toRemove = new ArrayList<Entity>();
        for (Entity entity : AutoCrystal.mc.world.loadedEntityList) {
            if (!(entity instanceof EntityEnderCrystal) || entity.getDistanceSq(pos) > MathUtil.square(range)) continue;
            toRemove.add(entity);
        }
        for (Entity entity : toRemove) {
            entity.setDead();
        }
    }

    private boolean predictSlowBreak(BlockPos pos) {
        return this.antiCommit.getValue() != false && lowDmgPos.remove(pos) && this.shouldSlowBreak(false);
    }

    private boolean isRightThread() {
        return mc.isCallingFromMinecraftThread() || !OctoHack.eventManager.ticksOngoing() && !this.threadOngoing.get();
    }

    private void attackCrystalPredict(int entityID, BlockPos pos) {
        if (!(!this.predictRotate.getValue().booleanValue() || this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE || this.rotate.getValue() != Rotate.BREAK && this.rotate.getValue() != Rotate.ALL)) {
            this.rotateToPos(pos);
        }
        CPacketUseEntity attackPacket = new CPacketUseEntity();
        attackPacket.entityId = entityID;
        attackPacket.action = CPacketUseEntity.Action.ATTACK;
        AutoCrystal.mc.player.connection.sendPacket(attackPacket);
        if (this.breakSwing.getValue().booleanValue()) {
            AutoCrystal.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
        }
        if (this.resetBreakTimer.getValue().booleanValue()) {
            this.breakTimer.reset();
        }
        this.predictTimer.reset();
    }

    private void removePos(BlockPos pos) {
        if (this.damageSync.getValue() == DamageSync.PLACE) {
            if (placedPos.remove(pos)) {
                this.posConfirmed = true;
            }
        } else if (this.damageSync.getValue() == DamageSync.BREAK && brokenPos.remove(pos)) {
            this.posConfirmed = true;
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        if ((this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC) && this.renderPos != null && this.render.getValue().booleanValue() && (this.box.getValue().booleanValue() || this.text.getValue().booleanValue() || this.outline.getValue().booleanValue())) {
            RenderUtil.drawBoxESP(this.renderPos, this.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.alpha.getValue()), this.customOutline.getValue(), this.colorSync.getValue() != false ? ClickGui.getInstance().getCurrentColor() : new Color(this.cRed.getValue(), this.cGreen.getValue(), this.cBlue.getValue(), this.cAlpha.getValue()), this.lineWidth.getValue().floatValue(), this.outline.getValue(), this.box.getValue(), this.boxAlpha.getValue(), false);
            if (this.text.getValue().booleanValue()) {
                RenderUtil.drawText(this.renderPos, (Math.floor(this.renderDamage) == this.renderDamage ? Integer.valueOf((int)this.renderDamage) : String.format("%.1f", this.renderDamage)) + "");
            }
        }
    }

    @SubscribeEvent
    public void onSettingChange(ClientEvent event) {
        if (event.getStage() == 2 && event.getSetting() != null && event.getSetting().getFeature() != null && event.getSetting().getFeature().equals(this) && this.isEnabled() && (event.getSetting().equals(this.threadDelay) || event.getSetting().equals(this.threadMode))) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            if (this.thread != null) {
                this.shouldInterrupt.set(true);
            }
        }
    }

    private void postProcessing() {
        if (this.threadMode.getValue() != ThreadMode.NONE || this.eventMode.getValue() != 2 || this.rotate.getValue() == Rotate.OFF || !this.rotateFirst.getValue().booleanValue()) {
            return;
        }
        switch (this.logic.getValue()) {
            case BREAKPLACE: {
                this.postProcessBreak();
                this.postProcessPlace();
                break;
            }
            case PLACEBREAK: {
                this.postProcessPlace();
                this.postProcessBreak();
            }
        }
    }

    private void postProcessBreak() {
        while (!this.packetUseEntities.isEmpty()) {
            CPacketUseEntity packet = this.packetUseEntities.poll();
            AutoCrystal.mc.player.connection.sendPacket(packet);
            if (this.breakSwing.getValue().booleanValue()) {
                AutoCrystal.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            this.breakTimer.reset();
        }
    }

    private void postProcessPlace() {
        if (this.placeInfo != null) {
            this.placeInfo.runPlace();
            this.placeTimer.reset();
            this.placeInfo = null;
        }
    }

    private void processMultiThreading() {
        if (this.isOff()) {
            return;
        }
        if (this.threadMode.getValue() == ThreadMode.WHILE) {
            this.handleWhile();
        } else if (this.threadMode.getValue() != ThreadMode.NONE) {
            this.handlePool(false);
        }
    }

    private void handlePool(boolean justDoIt) {
        if (justDoIt || this.executor == null || this.executor.isTerminated() || this.executor.isShutdown() || this.syncroTimer.passedMs(this.syncThreads.getValue().intValue()) && this.syncThreadBool.getValue().booleanValue()) {
            if (this.executor != null) {
                this.executor.shutdown();
            }
            this.executor = this.getExecutor();
            this.syncroTimer.reset();
        }
    }

    private void handleWhile() {
        if (this.thread == null || this.thread.isInterrupted() || !this.thread.isAlive() || this.syncroTimer.passedMs(this.syncThreads.getValue().intValue()) && this.syncThreadBool.getValue().booleanValue()) {
            if (this.thread == null) {
                this.thread = new Thread(RAutoCrystalRewrite.getInstance(this));
            } else if (this.syncroTimer.passedMs(this.syncThreads.getValue().intValue()) && !this.shouldInterrupt.get() && this.syncThreadBool.getValue().booleanValue()) {
                this.shouldInterrupt.set(true);
                this.syncroTimer.reset();
                return;
            }
            if (this.thread != null && (this.thread.isInterrupted() || !this.thread.isAlive())) {
                this.thread = new Thread(RAutoCrystalRewrite.getInstance(this));
            }
            if (this.thread != null && this.thread.getState() == Thread.State.NEW) {
                try {
                    this.thread.start();
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                this.syncroTimer.reset();
            }
        }
    }

    private ScheduledExecutorService getExecutor() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(RAutoCrystalRewrite.getInstance(this), 0L, this.threadDelay.getValue().intValue(), TimeUnit.MILLISECONDS);
        return service;
    }

    public void doAutoCrystalRewrite() {
        if (this.check()) {
            switch (this.logic.getValue()) {
                case PLACEBREAK: {
                    this.placeCrystal();
                    this.breakCrystal();
                    break;
                }
                case BREAKPLACE: {
                    this.breakCrystal();
                    this.placeCrystal();
                }
            }
            this.manualBreaker();
        }
    }

    private boolean check() {
        if (AutoCrystal.fullNullCheck()) {
            return false;
        }
        if (this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue())) {
            this.currentSyncTarget = null;
            this.syncedCrystalPos = null;
            this.syncedPlayerPos = null;
        } else if (this.syncySync.getValue().booleanValue() && this.syncedCrystalPos != null) {
            this.posConfirmed = true;
        }
        this.foundDoublePop = false;
        if (this.renderTimer.passedMs(500L)) {
            this.renderPos = null;
            this.renderTimer.reset();
        }
        boolean bl = this.mainHand = AutoCrystal.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;
        if (this.autoSwitch.getValue() == AutoSwitch.SILENT && InventoryUtil.getItemHotbar(Items.END_CRYSTAL) != -1) {
            this.mainHand = true;
            this.silentSwitch = true;
        } else {
            this.silentSwitch = false;
        }
        this.offHand = AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.currentDamage = 0.0;
        this.placePos = null;
        if (this.lastSlot != AutoCrystal.mc.player.inventory.currentItem) {
            this.lastSlot = AutoCrystal.mc.player.inventory.currentItem;
            this.switchTimer.reset();
        }
        this.offHand = AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        this.currentDamage = 0.0;
        this.placePos = null;
        if (this.lastSlot != AutoCrystal.mc.player.inventory.currentItem) {
            this.lastSlot = AutoCrystal.mc.player.inventory.currentItem;
            this.switchTimer.reset();
        }
        if (!this.offHand && !this.mainHand) {
            this.placeInfo = null;
            this.packetUseEntities.clear();
        }
        if (this.offHand || this.mainHand) {
            this.switching = false;
        }
        if (this.mineSwitch.getValue().booleanValue() && Mouse.isButtonDown(0) && (this.switching || this.autoSwitch.getValue() == AutoSwitch.ALWAYS) && Mouse.isButtonDown(1) && AutoCrystal.mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe) {
            this.switchItem();
        }
        this.mapCrystals();
        if (!this.posConfirmed && this.damageSync.getValue() != DamageSync.NONE && this.syncTimer.passedMs(this.confirm.getValue().intValue())) {
            this.syncTimer.setMs(this.damageSyncTime.getValue() + 1);
        }
        return true;
    }

    private void mapCrystals() {
        this.efficientTarget = null;
        if (this.packets.getValue() != 1) {
            this.attackList = new ConcurrentLinkedQueue<Entity>();
            this.crystalMap = new HashMap<Entity, Float>();
        }
        this.crystalCount = 0;
        this.minDmgCount = 0;
        Entity maxCrystal = null;
        float maxDamage = 0.5f;
        for (Entity entity : AutoCrystal.mc.world.loadedEntityList) {
            if (entity.isDead || !(entity instanceof EntityEnderCrystal) || !this.isValid(entity)) continue;
            if (this.syncedFeetPlace.getValue().booleanValue() && entity.getPosition().down().equals(this.syncedCrystalPos) && this.damageSync.getValue() != DamageSync.NONE) {
                ++this.minDmgCount;
                ++this.crystalCount;
                if (this.syncCount.getValue().booleanValue()) {
                    this.minDmgCount = this.wasteAmount.getValue() + 1;
                    this.crystalCount = this.wasteAmount.getValue() + 1;
                }
                if (!this.hyperSync.getValue().booleanValue()) continue;
                maxCrystal = null;
                break;
            }
            boolean count = false;
            boolean countMin = false;
            float selfDamage = -1.0f;
            if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                selfDamage = DamageUtil.calculateDamage(entity, (Entity)AutoCrystal.mc.player);
            }
            if ((double)selfDamage + 0.5 < (double)EntityUtil.getHealth(AutoCrystal.mc.player) && selfDamage <= this.maxSelfBreak.getValue().floatValue()) {
                Entity beforeCrystal = maxCrystal;
                float beforeDamage = maxDamage;
                for (EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                    float f;
                    float damage;
                    if (player.getDistanceSq(entity) > MathUtil.square(this.range.getValue().floatValue())) continue;
                    if (EntityUtil.isValid(player, this.range.getValue().floatValue() + this.breakRange.getValue().floatValue())) {
                        float f2;
                        if (this.antiNaked.getValue().booleanValue() && DamageUtil.isNaked(player)) continue;
                        damage = DamageUtil.calculateDamage(entity, (Entity)player);
                        if (f2 <= selfDamage && (damage <= this.minDamage.getValue().floatValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && damage <= EntityUtil.getHealth(player)) continue;
                        if (damage > maxDamage) {
                            maxDamage = damage;
                            maxCrystal = entity;
                        }
                        if (this.packets.getValue() == 1) {
                            if (damage >= this.minDamage.getValue().floatValue() || !this.wasteMinDmgCount.getValue().booleanValue()) {
                                count = true;
                            }
                            countMin = true;
                            continue;
                        }
                        if (this.crystalMap.get(entity) != null && this.crystalMap.get(entity).floatValue() >= damage) continue;
                        this.crystalMap.put(entity, Float.valueOf(damage));
                        continue;
                    }
                    if (this.antiFriendPop.getValue() != AntiFriendPop.BREAK && this.antiFriendPop.getValue() != AntiFriendPop.ALL || !OctoHack.friendManager.isFriend(player.getName())) continue;
                    damage = DamageUtil.calculateDamage(entity, (Entity)player);
                    if ((double)f <= (double)EntityUtil.getHealth(player) + 0.5) continue;
                    maxCrystal = beforeCrystal;
                    maxDamage = beforeDamage;
                    this.crystalMap.remove(entity);
                    if (!this.noCount.getValue().booleanValue()) break;
                    count = false;
                    countMin = false;
                    break;
                }
            }
            if (!countMin) continue;
            ++this.minDmgCount;
            if (!count) continue;
            ++this.crystalCount;
        }
        if (this.damageSync.getValue() == DamageSync.BREAK && ((double)maxDamage > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue()) || this.damageSync.getValue() == DamageSync.NONE)) {
            this.lastDamage = maxDamage;
        }
        if (this.enormousSync.getValue().booleanValue() && this.syncedFeetPlace.getValue().booleanValue() && this.damageSync.getValue() != DamageSync.NONE && this.syncedCrystalPos != null) {
            if (this.syncCount.getValue().booleanValue()) {
                this.minDmgCount = this.wasteAmount.getValue() + 1;
                this.crystalCount = this.wasteAmount.getValue() + 1;
            }
            return;
        }
        if (this.webAttack.getValue().booleanValue() && this.webPos != null) {
            if (AutoCrystal.mc.player.getDistanceSq(this.webPos.up()) > MathUtil.square(this.breakRange.getValue().floatValue())) {
                this.webPos = null;
            } else {
                for (Entity entity : AutoCrystal.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(this.webPos.up()))) {
                    if (!(entity instanceof EntityEnderCrystal)) continue;
                    this.attackList.add(entity);
                    this.efficientTarget = entity;
                    this.webPos = null;
                    this.lastDamage = 0.5;
                    return;
                }
            }
        }
        if (this.shouldSlowBreak(true) && maxDamage < this.minDamage.getValue().floatValue() && (target == null || EntityUtil.getHealth(target) > this.facePlace.getValue().floatValue() || !this.breakTimer.passedMs(this.facePlaceSpeed.getValue().intValue()) && this.slowFaceBreak.getValue().booleanValue() && Mouse.isButtonDown(0) && this.holdFacePlace.getValue().booleanValue() && this.holdFaceBreak.getValue().booleanValue())) {
            this.efficientTarget = null;
            return;
        }
        if (this.packets.getValue() == 1) {
            this.efficientTarget = maxCrystal;
        } else {
            this.crystalMap = MathUtil.sortByValue(this.crystalMap, true);
            for (Map.Entry entry : this.crystalMap.entrySet()) {
                Entity crystal = (Entity)entry.getKey();
                float damage2 = ((Float)entry.getValue()).floatValue();
                if (damage2 >= this.minDamage.getValue().floatValue() || !this.wasteMinDmgCount.getValue().booleanValue()) {
                    ++this.crystalCount;
                }
                this.attackList.add(crystal);
                ++this.minDmgCount;
            }
        }
    }

    private boolean shouldSlowBreak(boolean withManual) {
        return withManual && this.manual.getValue() != false && this.manualMinDmg.getValue() != false && Mouse.isButtonDown(1) && (!Mouse.isButtonDown(0) || this.holdFacePlace.getValue() == false) || this.holdFacePlace.getValue() != false && this.holdFaceBreak.getValue() != false && Mouse.isButtonDown(0) && !this.breakTimer.passedMs(this.facePlaceSpeed.getValue().intValue()) || this.slowFaceBreak.getValue() != false && !this.breakTimer.passedMs(this.facePlaceSpeed.getValue().intValue());
    }

    private void placeCrystal() {
        int crystalLimit = this.wasteAmount.getValue();
        if (this.placeTimer.passedMs(this.placeDelay.getValue().intValue()) && this.place.getValue().booleanValue() && (this.offHand || this.mainHand || this.switchMode.getValue() == Switch.CALC || this.switchMode.getValue() == Switch.BREAKSLOT && this.switching)) {
            if (!(!this.offHand && !this.mainHand && (this.switchMode.getValue() == Switch.ALWAYS || this.switching) || this.crystalCount < crystalLimit || this.antiSurround.getValue().booleanValue() && this.lastPos != null && this.lastPos.equals(this.placePos))) {
                return;
            }
            this.calculateDamage(this.getTarget(this.targetMode.getValue() == Target.UNSAFE));
            if (target != null && this.placePos != null) {
                if (!this.offHand && !this.mainHand && this.autoSwitch.getValue() != AutoSwitch.NONE && (this.currentDamage > (double)this.minDamage.getValue().floatValue() || this.lethalSwitch.getValue().booleanValue() && EntityUtil.getHealth(target) <= this.facePlace.getValue().floatValue()) && !this.switchItem()) {
                    return;
                }
                if (this.currentDamage < (double)this.minDamage.getValue().floatValue() && this.limitFacePlace.getValue().booleanValue()) {
                    crystalLimit = 1;
                }
                if (this.currentDamage >= (double)this.minMinDmg.getValue().floatValue() && (this.offHand || this.mainHand || this.autoSwitch.getValue() != AutoSwitch.NONE) && (this.crystalCount < crystalLimit || this.antiSurround.getValue().booleanValue() && this.lastPos != null && this.lastPos.equals(this.placePos)) && (this.currentDamage > (double)this.minDamage.getValue().floatValue() || this.minDmgCount < crystalLimit) && this.currentDamage >= 1.0 && (DamageUtil.isArmorLow(target, this.minArmor.getValue()) || EntityUtil.getHealth(target) <= this.facePlace.getValue().floatValue() || this.currentDamage > (double)this.minDamage.getValue().floatValue() || this.shouldHoldFacePlace())) {
                    float damageOffset = this.damageSync.getValue() == DamageSync.BREAK ? this.dropOff.getValue().floatValue() - 5.0f : 0.0f;
                    boolean syncflag = false;
                    if (this.syncedFeetPlace.getValue().booleanValue() && this.placePos.equals(this.lastPos) && this.isEligableForFeetSync(target, this.placePos) && !this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue()) && target.equals(this.currentSyncTarget) && target.getPosition().equals(this.syncedPlayerPos) && this.damageSync.getValue() != DamageSync.NONE) {
                        this.syncedCrystalPos = this.placePos;
                        this.lastDamage = this.currentDamage;
                        if (this.fullSync.getValue().booleanValue()) {
                            this.lastDamage = 100.0;
                        }
                        syncflag = true;
                    }
                    if (syncflag || this.currentDamage - (double)damageOffset > this.lastDamage || this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue()) || this.damageSync.getValue() == DamageSync.NONE) {
                        if (!syncflag && this.damageSync.getValue() != DamageSync.BREAK) {
                            this.lastDamage = this.currentDamage;
                        }
                        this.renderPos = this.placePos;
                        this.renderDamage = this.currentDamage;
                        if (this.switchItem()) {
                            this.currentSyncTarget = target;
                            this.syncedPlayerPos = target.getPosition();
                            if (this.foundDoublePop) {
                                this.totemPops.put(target, new Timer().reset());
                            }
                            this.rotateToPos(this.placePos);
                            if (this.addTolowDmg || this.actualSlowBreak.getValue().booleanValue() && this.currentDamage < (double)this.minDamage.getValue().floatValue()) {
                                lowDmgPos.add(this.placePos);
                            }
                            placedPos.add(this.placePos);
                            if (!this.justRender.getValue().booleanValue()) {
                                if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && this.rotateFirst.getValue().booleanValue() && this.rotate.getValue() != Rotate.OFF) {
                                    this.placeInfo = new PlaceInfo(this.placePos, this.offHand, this.placeSwing.getValue(), this.exactHand.getValue(), this.silentSwitch);
                                } else {
                                    BlockUtil.placeCrystalOnBlock(this.placePos, this.offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing.getValue(), this.exactHand.getValue(), this.silentSwitch);
                                }
                            }
                            this.lastPos = this.placePos;
                            this.placeTimer.reset();
                            this.posConfirmed = false;
                            if (this.syncTimer.passedMs(this.damageSyncTime.getValue().intValue())) {
                                this.syncedCrystalPos = null;
                                this.syncTimer.reset();
                            }
                        }
                    }
                }
            } else {
                this.renderPos = null;
            }
        }
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    private boolean shouldHoldFacePlace() {
        this.addTolowDmg = false;
        if (this.holdFacePlace.getValue() == false) return false;
        if (!Mouse.isButtonDown(0)) return false;
        this.addTolowDmg = true;
        if (!true) return false;
        return true;
    }

    private boolean switchItem() {
        if (this.offHand || this.mainHand) {
            return true;
        }
        switch (this.autoSwitch.getValue()) {
            case NONE: {
                return false;
            }
            case TOGGLE: {
                if (!this.switching) {
                    return false;
                }
            }
            case ALWAYS: {
                if (!this.doSwitch()) break;
                return true;
            }
        }
        return false;
    }

    private boolean doSwitch() {
        if (AutoCrystal.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            this.mainHand = false;
        } else {
            InventoryUtil.switchToHotbarSlot(ItemEndCrystal.class, false);
            this.mainHand = true;
        }
        this.switching = false;
        return true;
    }

    private void calculateDamage(EntityPlayer targettedPlayer) {
        BlockPos playerPos;
        Block web;
        if (targettedPlayer == null && this.targetMode.getValue() != Target.DAMAGE && !this.fullCalc.getValue().booleanValue()) {
            return;
        }
        float maxDamage = 0.5f;
        EntityPlayer currentTarget = null;
        BlockPos currentPos = null;
        float maxSelfDamage = 0.0f;
        this.foundDoublePop = false;
        BlockPos setToAir = null;
        IBlockState state = null;
        if (this.webAttack.getValue().booleanValue() && targettedPlayer != null && (web = AutoCrystal.mc.world.getBlockState(playerPos = new BlockPos(targettedPlayer.getPositionVector())).getBlock()) == Blocks.WEB) {
            setToAir = playerPos;
            state = AutoCrystal.mc.world.getBlockState(playerPos);
            AutoCrystal.mc.world.setBlockToAir(playerPos);
        }
        block0: for (BlockPos pos : BlockUtil.possiblePlacePosition(this.placeRange.getValue().floatValue(), this.antiSurround.getValue(), this.oneDot15.getValue())) {
            if (!BlockUtil.rayTracePlaceCheck(pos, (this.raytrace.getValue() == Raytrace.PLACE || this.raytrace.getValue() == Raytrace.FULL) && AutoCrystal.mc.player.getDistanceSq(pos) > MathUtil.square(this.placetrace.getValue().floatValue()), 1.0f)) continue;
            float selfDamage = -1.0f;
            if (DamageUtil.canTakeDamage(this.suicide.getValue())) {
                selfDamage = DamageUtil.calculateDamage(pos, (Entity)AutoCrystal.mc.player);
            }
            if ((double)selfDamage + 0.5 >= (double)EntityUtil.getHealth(AutoCrystal.mc.player) || selfDamage > this.maxSelfPlace.getValue().floatValue()) continue;
            if (targettedPlayer != null) {
                float playerDamage = DamageUtil.calculateDamage(pos, (Entity)targettedPlayer);
                if (this.calcEvenIfNoDamage.getValue().booleanValue() && (this.antiFriendPop.getValue() == AntiFriendPop.ALL || this.antiFriendPop.getValue() == AntiFriendPop.PLACE)) {
                    boolean friendPop = false;
                    for (EntityPlayer friend : AutoCrystal.mc.world.playerEntities) {
                        float f;
                        if (friend == null || AutoCrystal.mc.player.equals(friend) || !(friend.getDistanceSq(pos) <= MathUtil.square(this.range.getValue().floatValue() + this.placeRange.getValue().floatValue())) || !OctoHack.friendManager.isFriend(friend)) continue;
                        float friendDamage = DamageUtil.calculateDamage(pos, (Entity)friend);
                        if ((double)f <= (double)EntityUtil.getHealth(friend) + 0.5) continue;
                        friendPop = true;
                        break;
                    }
                    if (friendPop) continue;
                }
                if (this.isDoublePoppable(targettedPlayer, playerDamage) && (currentPos == null || targettedPlayer.getDistanceSq(pos) < targettedPlayer.getDistanceSq(currentPos))) {
                    currentTarget = targettedPlayer;
                    maxDamage = playerDamage;
                    currentPos = pos;
                    this.foundDoublePop = true;
                    continue;
                }
                if (this.foundDoublePop || playerDamage <= maxDamage && (!this.extraSelfCalc.getValue().booleanValue() || playerDamage < maxDamage || selfDamage >= maxSelfDamage) || playerDamage <= selfDamage && (playerDamage <= this.minDamage.getValue().floatValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage <= EntityUtil.getHealth(targettedPlayer)) continue;
                maxDamage = playerDamage;
                currentTarget = targettedPlayer;
                currentPos = pos;
                maxSelfDamage = selfDamage;
                continue;
            }
            float maxDamageBefore = maxDamage;
            EntityPlayer currentTargetBefore = currentTarget;
            BlockPos currentPosBefore = currentPos;
            float maxSelfDamageBefore = maxSelfDamage;
            for (EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
                float f;
                if (EntityUtil.isValid(player, this.placeRange.getValue().floatValue() + this.range.getValue().floatValue())) {
                    if (this.antiNaked.getValue().booleanValue() && DamageUtil.isNaked(player)) continue;
                    float playerDamage2 = DamageUtil.calculateDamage(pos, (Entity)player);
                    if (this.doublePopOnDamage.getValue().booleanValue() && this.isDoublePoppable(player, playerDamage2) && (currentPos == null || player.getDistanceSq(pos) < player.getDistanceSq(currentPos))) {
                        currentTarget = player;
                        maxDamage = playerDamage2;
                        currentPos = pos;
                        maxSelfDamage = selfDamage;
                        this.foundDoublePop = true;
                        if (this.antiFriendPop.getValue() == AntiFriendPop.BREAK || this.antiFriendPop.getValue() == AntiFriendPop.PLACE) continue block0;
                        continue;
                    }
                    if (this.foundDoublePop || playerDamage2 <= maxDamage && (!this.extraSelfCalc.getValue().booleanValue() || playerDamage2 < maxDamage || selfDamage >= maxSelfDamage) || playerDamage2 <= selfDamage && (playerDamage2 <= this.minDamage.getValue().floatValue() || DamageUtil.canTakeDamage(this.suicide.getValue())) && playerDamage2 <= EntityUtil.getHealth(player)) continue;
                    maxDamage = playerDamage2;
                    currentTarget = player;
                    currentPos = pos;
                    maxSelfDamage = selfDamage;
                    continue;
                }
                if (this.antiFriendPop.getValue() != AntiFriendPop.ALL && this.antiFriendPop.getValue() != AntiFriendPop.PLACE || player == null || player.getDistanceSq(pos) > MathUtil.square(this.range.getValue().floatValue() + this.placeRange.getValue().floatValue()) || !OctoHack.friendManager.isFriend(player)) continue;
                float friendDamage2 = DamageUtil.calculateDamage(pos, (Entity)player);
                if ((double)f <= (double)EntityUtil.getHealth(player) + 0.5) continue;
                maxDamage = maxDamageBefore;
                currentTarget = currentTargetBefore;
                currentPos = currentPosBefore;
                maxSelfDamage = maxSelfDamageBefore;
                continue block0;
            }
        }
        if (setToAir != null) {
            AutoCrystal.mc.world.setBlockState(setToAir, state);
            this.webPos = currentPos;
        }
        target = currentTarget;
        this.currentDamage = maxDamage;
        this.placePos = currentPos;
    }

    private EntityPlayer getTarget(boolean unsafe) {
        if (this.targetMode.getValue() == Target.DAMAGE) {
            return null;
        }
        EntityPlayer currentTarget = null;
        for (EntityPlayer player : AutoCrystal.mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, this.placeRange.getValue().floatValue() + this.range.getValue().floatValue()) || this.antiNaked.getValue().booleanValue() && DamageUtil.isNaked(player) || unsafe && EntityUtil.isSafe(player)) continue;
            if (this.minArmor.getValue() > 0 && DamageUtil.isArmorLow(player, this.minArmor.getValue())) {
                currentTarget = player;
                break;
            }
            if (currentTarget == null) {
                currentTarget = player;
                continue;
            }
            if (AutoCrystal.mc.player.getDistanceSq(player) >= AutoCrystal.mc.player.getDistanceSq(currentTarget)) continue;
            currentTarget = player;
        }
        if (unsafe && currentTarget == null) {
            return this.getTarget(false);
        }
        if (this.predictPos.getValue().booleanValue() && currentTarget != null) {
            GameProfile profile = new GameProfile(currentTarget.getUniqueID() == null ? UUID.fromString("8af022c8-b926-41a0-8b79-2b544ff00fcf") : currentTarget.getUniqueID(), currentTarget.getName());
            EntityOtherPlayerMP newTarget = new EntityOtherPlayerMP(AutoCrystal.mc.world, profile);
            Vec3d extrapolatePosition = MathUtil.extrapolatePlayerPosition(currentTarget, (int)this.predictTicks.getValue());
            newTarget.copyLocationAndAnglesFrom(currentTarget);
            newTarget.posX = extrapolatePosition.x;
            newTarget.posY = extrapolatePosition.y;
            newTarget.posZ = extrapolatePosition.z;
            newTarget.setHealth(EntityUtil.getHealth(currentTarget));
            newTarget.inventory.copyInventory(currentTarget.inventory);
            currentTarget = newTarget;
        }
        return currentTarget;
    }

    private void breakCrystal() {
        int swordSlot = InventoryUtil.findHotbarBlock(ItemSword.class);
        int oldSlot = AutoCrystal.mc.player.inventory.currentItem;
        if (this.explode.getValue().booleanValue() && this.breakTimer.passedMs(this.breakDelay.getValue().intValue()) && (this.switchMode.getValue() == Switch.ALWAYS || this.mainHand || this.offHand)) {
            if (this.packets.getValue() == 1 && this.efficientTarget != null) {
                if (this.justRender.getValue().booleanValue()) {
                    this.doFakeSwing();
                    return;
                }
                if (this.syncedFeetPlace.getValue().booleanValue() && this.gigaSync.getValue().booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
                    return;
                }
                this.rotateTo(this.efficientTarget);
                if (!this.offHand && !this.mainHand && this.switchMode.getValue() == Switch.BREAKSLOT && !this.switching || !DamageUtil.canBreakWeakness(AutoCrystal.mc.player) || !this.switchTimer.passedMs(this.switchCooldown.getValue().intValue()) && this.antiWeakness.getValue() != AntiWeaknessMode.None) {
                    if (this.antiWeakness.getValue() == AntiWeaknessMode.Silent) {
                        AutoCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(swordSlot));
                        this.attackEntity(this.efficientTarget);
                        AutoCrystal.mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
                    }
                    if (this.antiWeakness.getValue() == AntiWeaknessMode.Normal) {
                        InventoryUtil.switchToHotbarSlot(swordSlot, false);
                        this.attackEntity(this.efficientTarget);
                        InventoryUtil.switchToHotbarSlot(oldSlot, false);
                    }
                } else {
                    this.attackEntity(this.efficientTarget);
                }
                this.breakTimer.reset();
            } else if (!this.attackList.isEmpty()) {
                if (this.justRender.getValue().booleanValue()) {
                    this.doFakeSwing();
                    return;
                }
                if (this.syncedFeetPlace.getValue().booleanValue() && this.gigaSync.getValue().booleanValue() && this.syncedCrystalPos != null && this.damageSync.getValue() != DamageSync.NONE) {
                    return;
                }
                for (int i = 0; i < this.packets.getValue(); ++i) {
                    Entity entity = this.attackList.poll();
                    if (entity == null) continue;
                    this.rotateTo(entity);
                    this.attackEntity(entity);
                }
                this.breakTimer.reset();
            }
        }
    }

    private void attackEntity(Entity entity) {
        if (entity != null) {
            if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE && this.rotateFirst.getValue().booleanValue() && this.rotate.getValue() != Rotate.OFF) {
                this.packetUseEntities.add(new CPacketUseEntity(entity));
            } else {
                EntityUtil.attackEntity(entity, this.sync.getValue(), this.breakSwing.getValue());
                brokenPos.add(new BlockPos(entity.getPositionVector()).down());
            }
        }
    }

    private void doFakeSwing() {
        if (this.fakeSwing.getValue().booleanValue()) {
            EntityUtil.swingArmNoPacket(EnumHand.MAIN_HAND, AutoCrystal.mc.player);
        }
    }

    private void manualBreaker() {
        RayTraceResult result;
        if (this.rotate.getValue() != Rotate.OFF && this.eventMode.getValue() != 2 && this.rotating) {
            if (this.didRotation) {
                AutoCrystal.mc.player.rotationPitch += 4.0E-4f;
                this.didRotation = false;
            } else {
                AutoCrystal.mc.player.rotationPitch -= 4.0E-4f;
                this.didRotation = true;
            }
        }
        if ((this.offHand || this.mainHand) && this.manual.getValue().booleanValue() && this.manualTimer.passedMs(this.manualBreak.getValue().intValue()) && Mouse.isButtonDown(1) && AutoCrystal.mc.player.getHeldItemOffhand().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.GOLDEN_APPLE && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.BOW && AutoCrystal.mc.player.inventory.getCurrentItem().getItem() != Items.EXPERIENCE_BOTTLE && (result = AutoCrystal.mc.objectMouseOver) != null) {
            switch (result.typeOfHit) {
                case ENTITY: {
                    Entity entity = result.entityHit;
                    if (!(entity instanceof EntityEnderCrystal)) break;
                    EntityUtil.attackEntity(entity, this.sync.getValue(), this.breakSwing.getValue());
                    this.manualTimer.reset();
                    break;
                }
                case BLOCK: {
                    BlockPos mousePos = AutoCrystal.mc.objectMouseOver.getBlockPos().up();
                    for (Entity target : AutoCrystal.mc.world.getEntitiesWithinAABBExcludingEntity(null, new AxisAlignedBB(mousePos))) {
                        if (!(target instanceof EntityEnderCrystal)) continue;
                        EntityUtil.attackEntity(target, this.sync.getValue(), this.breakSwing.getValue());
                        this.manualTimer.reset();
                    }
                    break;
                }
            }
        }
    }

    private void rotateTo(Entity entity) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case BREAK: 
            case ALL: {
                float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), entity.getPositionVector());
                if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE) {
                    OctoHack.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
                break;
            }
        }
    }

    private void rotateToPos(BlockPos pos) {
        switch (this.rotate.getValue()) {
            case OFF: {
                this.rotating = false;
            }
            case ALL: 
            case PLACE: {
                float[] angle = MathUtil.calcAngle(AutoCrystal.mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d((float)pos.getX() + 0.5f, (float)pos.getY() - 0.5f, (float)pos.getZ() + 0.5f));
                if (this.eventMode.getValue() == 2 && this.threadMode.getValue() == ThreadMode.NONE) {
                    OctoHack.rotationManager.setPlayerRotations(angle[0], angle[1]);
                    break;
                }
                this.yaw = angle[0];
                this.pitch = angle[1];
                this.rotating = true;
                break;
            }
        }
    }

    private boolean isDoublePoppable(EntityPlayer player, float damage) {
        if (this.doublePop.getValue().booleanValue()) {
            float f;
            float health = EntityUtil.getHealth(player);
            if ((double)f <= this.popHealth.getValue() && (double)damage > (double)health + 0.5 && damage <= this.popDamage.getValue().floatValue()) {
                Timer timer = this.totemPops.get(player);
                return timer == null || timer.passedMs(this.popTime.getValue().intValue());
            }
        }
        return false;
    }

    private boolean isValid(Entity entity) {
        return entity != null && AutoCrystal.mc.player.getDistanceSq(entity) <= MathUtil.square(this.breakRange.getValue().floatValue()) && (this.raytrace.getValue() == Raytrace.NONE || this.raytrace.getValue() == Raytrace.PLACE || AutoCrystal.mc.player.canEntityBeSeen(entity) || !AutoCrystal.mc.player.canEntityBeSeen(entity) && AutoCrystal.mc.player.getDistanceSq(entity) <= MathUtil.square(this.breaktrace.getValue().floatValue()));
    }

    private boolean isEligableForFeetSync(EntityPlayer player, BlockPos pos) {
        if (this.holySync.getValue().booleanValue()) {
            BlockPos playerPos = new BlockPos(player.getPositionVector());
            for (EnumFacing facing : EnumFacing.values()) {
                BlockPos holyPos;
                if (facing == EnumFacing.DOWN || facing == EnumFacing.UP || !pos.equals(holyPos = playerPos.down().offset(facing))) continue;
                return true;
            }
            return false;
        }
        return true;
    }

    private static class RAutoCrystalRewrite
    implements Runnable {
        private static RAutoCrystalRewrite instance;
        private AutoCrystal AutoCrystalRewrite;

        private RAutoCrystalRewrite() {
        }

        public static RAutoCrystalRewrite getInstance(AutoCrystal AutoCrystalRewrite) {
            if (instance == null) {
                instance = new RAutoCrystalRewrite();
                RAutoCrystalRewrite.instance.AutoCrystalRewrite = AutoCrystalRewrite;
            }
            return instance;
        }

        @Override
        public void run() {
            if (this.AutoCrystalRewrite.threadMode.getValue() == ThreadMode.WHILE) {
                while (this.AutoCrystalRewrite.isOn() && this.AutoCrystalRewrite.threadMode.getValue() == ThreadMode.WHILE) {
                    while (OctoHack.eventManager.ticksOngoing()) {
                    }
                    if (this.AutoCrystalRewrite.shouldInterrupt.get()) {
                        this.AutoCrystalRewrite.shouldInterrupt.set(false);
                        this.AutoCrystalRewrite.syncroTimer.reset();
                        this.AutoCrystalRewrite.thread.interrupt();
                        break;
                    }
                    this.AutoCrystalRewrite.threadOngoing.set(true);
                    OctoHack.safetyManager.doSafetyCheck();
                    this.AutoCrystalRewrite.doAutoCrystalRewrite();
                    this.AutoCrystalRewrite.threadOngoing.set(false);
                    try {
                        Thread.sleep(this.AutoCrystalRewrite.threadDelay.getValue().intValue());
                    }
                    catch (InterruptedException e) {
                        this.AutoCrystalRewrite.thread.interrupt();
                        e.printStackTrace();
                    }
                }
            } else if (this.AutoCrystalRewrite.threadMode.getValue() != ThreadMode.NONE && this.AutoCrystalRewrite.isOn()) {
                while (OctoHack.eventManager.ticksOngoing()) {
                }
                this.AutoCrystalRewrite.threadOngoing.set(true);
                OctoHack.safetyManager.doSafetyCheck();
                this.AutoCrystalRewrite.doAutoCrystalRewrite();
                this.AutoCrystalRewrite.threadOngoing.set(false);
            }
        }
    }

    public static class PlaceInfo {
        private final BlockPos pos;
        private final boolean offhand;
        private final boolean placeSwing;
        private final boolean exactHand;
        private final boolean silent;

        public PlaceInfo(BlockPos pos, boolean offhand, boolean placeSwing, boolean exactHand, boolean silent) {
            this.pos = pos;
            this.offhand = offhand;
            this.placeSwing = placeSwing;
            this.exactHand = exactHand;
            this.silent = silent;
        }

        public void runPlace() {
            BlockUtil.placeCrystalOnBlock(this.pos, this.offhand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, this.placeSwing, this.exactHand, this.silent);
        }
    }

    public static enum Settings {
        PLACE,
        BREAK,
        RENDER,
        MISC,
        DEV;

    }

    public static enum DamageSync {
        NONE,
        PLACE,
        BREAK;

    }

    public static enum Rotate {
        OFF,
        PLACE,
        BREAK,
        ALL;

    }

    public static enum Target {
        CLOSEST,
        UNSAFE,
        DAMAGE;

    }

    public static enum Logic {
        BREAKPLACE,
        PLACEBREAK;

    }

    public static enum Switch {
        ALWAYS,
        BREAKSLOT,
        CALC;

    }

    public static enum Raytrace {
        NONE,
        PLACE,
        BREAK,
        FULL;

    }

    public static enum AutoSwitch {
        NONE,
        TOGGLE,
        ALWAYS,
        SILENT;

    }

    public static enum ThreadMode {
        NONE,
        POOL,
        SOUND,
        WHILE;

    }

    public static enum AntiFriendPop {
        NONE,
        PLACE,
        BREAK,
        ALL;

    }

    public static enum PredictTimer {
        NONE,
        BREAK,
        PREDICT;

    }

    public static enum Timing {
        Sequential,
        None;

    }

    public static enum AntiWeaknessMode {
        Normal,
        Silent,
        None;

    }
}
