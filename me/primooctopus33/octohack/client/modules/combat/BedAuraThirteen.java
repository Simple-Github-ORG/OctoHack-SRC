package me.primooctopus33.octohack.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import java.util.Comparator;
import java.util.List;
import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.command.Command;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.ColorUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemBed;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntityBed;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BedAuraThirteen
extends Module {
    public final Setting<Integer> range = this.register(new Setting<Integer>("Range", 6, 0, 9));
    public final Setting<Integer> placedelay = this.register(new Setting<Integer>("Place Delay", 15, 8, 20));
    public final Setting<Boolean> announceUsage = this.register(new Setting<Boolean>("Toggle Messages", false));
    public final Setting<Boolean> placeesp = this.register(new Setting<Boolean>("Render Placements", true));
    private int playerHotbarSlot = -1;
    private int lastHotbarSlot = -1;
    private EntityPlayer closestTarget;
    private String lastTickTargetName;
    private int bedSlot = -1;
    private BlockPos placeTarget;
    private float rotVar;
    private int blocksPlaced;
    private double diffXZ;
    private boolean firstRun;
    private boolean nowTop = false;

    public BedAuraThirteen() {
        super("BedAura1.13", "Automatically places and breaks Beds for 1.13 Servers", Module.Category.COMBAT, true, false, false);
    }

    @Override
    public void onEnable() {
        if (BedAuraThirteen.mc.player == null) {
            this.toggle();
            return;
        }
        MinecraftForge.EVENT_BUS.register(this);
        this.firstRun = true;
        this.blocksPlaced = 0;
        this.playerHotbarSlot = BedAuraThirteen.mc.player.inventory.currentItem;
        this.lastHotbarSlot = -1;
    }

    @Override
    public void onDisable() {
        if (BedAuraThirteen.mc.player == null) {
            return;
        }
        MinecraftForge.EVENT_BUS.unregister(this);
        if (this.lastHotbarSlot != this.playerHotbarSlot && this.playerHotbarSlot != -1) {
            BedAuraThirteen.mc.player.inventory.currentItem = this.playerHotbarSlot;
        }
        this.playerHotbarSlot = -1;
        this.lastHotbarSlot = -1;
        if (this.announceUsage.getValue().booleanValue()) {
            Command.sendMessage("BedAuraTwo Disabled");
        }
        this.blocksPlaced = 0;
    }

    @Override
    public void onUpdate() {
        if (BedAuraThirteen.mc.player == null) {
            return;
        }
        if (BedAuraThirteen.mc.player.dimension == 0) {
            Command.sendMessage(ChatFormatting.WHITE + "<DimensionCheck> You are in the Overworld! Toggling Off!");
            this.disable();
        }
        try {
            this.findClosestTarget();
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        if (this.closestTarget == null && BedAuraThirteen.mc.player.dimension != 0 && this.firstRun) {
            this.firstRun = false;
            if (this.announceUsage.getValue().booleanValue()) {
                Command.sendMessage("BedAuraTwo Enabled | Waiting for Target");
            }
        }
        if (this.firstRun && this.closestTarget != null && BedAuraThirteen.mc.player.dimension != 0) {
            this.firstRun = false;
            this.lastTickTargetName = this.closestTarget.getName();
            if (this.announceUsage.getValue().booleanValue()) {
                Command.sendMessage("BedAuraTwo Enabled | Current Target:" + this.lastTickTargetName);
            }
        }
        if (this.closestTarget != null && this.lastTickTargetName != null && !this.lastTickTargetName.equals(this.closestTarget.getName())) {
            this.lastTickTargetName = this.closestTarget.getName();
            if (this.announceUsage.getValue().booleanValue()) {
                Command.sendMessage("BedAuraTwo Enabled | New Target:" + this.lastTickTargetName);
            }
        }
        try {
            this.diffXZ = BedAuraThirteen.mc.player.getPositionVector().distanceTo(this.closestTarget.getPositionVector());
        }
        catch (NullPointerException nullPointerException) {
            // empty catch block
        }
        try {
            if (this.closestTarget != null) {
                BlockPos block4;
                BlockPos block3;
                BlockPos block2;
                this.placeTarget = new BlockPos(this.closestTarget.getPositionVector().addVector(1.0, 1.0, 0.0));
                this.nowTop = false;
                this.rotVar = 90.0f;
                BlockPos block1 = this.placeTarget;
                if (!this.canPlaceBed(block1)) {
                    this.placeTarget = new BlockPos(this.closestTarget.getPositionVector().addVector(-1.0, 1.0, 0.0));
                    this.rotVar = -90.0f;
                    this.nowTop = false;
                }
                if (!this.canPlaceBed(block2 = this.placeTarget)) {
                    this.placeTarget = new BlockPos(this.closestTarget.getPositionVector().addVector(0.0, 1.0, 1.0));
                    this.rotVar = 180.0f;
                    this.nowTop = false;
                }
                if (!this.canPlaceBed(block3 = this.placeTarget)) {
                    this.placeTarget = new BlockPos(this.closestTarget.getPositionVector().addVector(0.0, 1.0, -1.0));
                    this.rotVar = 0.0f;
                    this.nowTop = false;
                }
                if (!this.canPlaceBed(block4 = this.placeTarget)) {
                    this.placeTarget = new BlockPos(this.closestTarget.getPositionVector().addVector(0.0, 2.0, -1.0));
                    this.rotVar = 0.0f;
                    this.nowTop = true;
                }
                BlockPos blockt1 = this.placeTarget;
                if (this.nowTop && !this.canPlaceBed(blockt1)) {
                    this.placeTarget = new BlockPos(this.closestTarget.getPositionVector().addVector(-1.0, 2.0, 0.0));
                    this.rotVar = -90.0f;
                }
                BlockPos blockt2 = this.placeTarget;
                if (this.nowTop && !this.canPlaceBed(blockt2)) {
                    this.placeTarget = new BlockPos(this.closestTarget.getPositionVector().addVector(0.0, 2.0, 1.0));
                    this.rotVar = 180.0f;
                }
                BlockPos blockt3 = this.placeTarget;
                if (this.nowTop && !this.canPlaceBed(blockt3)) {
                    this.placeTarget = new BlockPos(this.closestTarget.getPositionVector().addVector(1.0, 2.0, 0.0));
                    this.rotVar = 90.0f;
                }
            }
            BedAuraThirteen.mc.world.loadedTileEntityList.stream().filter(e -> e instanceof TileEntityBed).filter(e -> BedAuraThirteen.mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ()) <= (double)this.range.getValue().intValue()).sorted(Comparator.comparing(e -> BedAuraThirteen.mc.player.getDistance(e.getPos().getX(), e.getPos().getY(), e.getPos().getZ()))).forEach(bed -> {
                if (BedAuraThirteen.mc.player.dimension != 0) {
                    BedAuraThirteen.mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(bed.getPos(), EnumFacing.UP, EnumHand.OFF_HAND, 0.0f, 0.0f, 0.0f));
                }
            });
            if (BedAuraThirteen.mc.player.ticksExisted % this.placedelay.getValue() == 0 && this.closestTarget != null) {
                this.findBeds();
                ++BedAuraThirteen.mc.player.ticksExisted;
                this.doDaMagic();
            }
        }
        catch (NullPointerException npe) {
            npe.printStackTrace();
        }
    }

    private void doDaMagic() {
        if (this.diffXZ <= (double)this.range.getValue().intValue()) {
            for (int i = 0; i < 9 && this.bedSlot == -1; ++i) {
                ItemStack stack = BedAuraThirteen.mc.player.inventory.getStackInSlot(i);
                if (!(stack.getItem() instanceof ItemBed)) continue;
                this.bedSlot = i;
                if (i == -1) break;
                BedAuraThirteen.mc.player.inventory.currentItem = this.bedSlot;
                break;
            }
            this.bedSlot = -1;
            if (this.blocksPlaced == 0 && BedAuraThirteen.mc.player.inventory.getStackInSlot(BedAuraThirteen.mc.player.inventory.currentItem).getItem() instanceof ItemBed) {
                BedAuraThirteen.mc.player.connection.sendPacket(new CPacketEntityAction(BedAuraThirteen.mc.player, CPacketEntityAction.Action.START_SNEAKING));
                BedAuraThirteen.mc.player.connection.sendPacket(new CPacketPlayer.Rotation(this.rotVar, 0.0f, BedAuraThirteen.mc.player.onGround));
                this.placeBlock(new BlockPos(this.placeTarget), EnumFacing.DOWN);
                BedAuraThirteen.mc.player.connection.sendPacket(new CPacketEntityAction(BedAuraThirteen.mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                this.blocksPlaced = 1;
                this.nowTop = false;
            }
            this.blocksPlaced = 0;
        }
    }

    private void findBeds() {
        if (!(BedAuraThirteen.mc.currentScreen != null && BedAuraThirteen.mc.currentScreen instanceof GuiContainer || BedAuraThirteen.mc.player.inventory.getStackInSlot(0).getItem() == Items.BED)) {
            for (int i = 9; i < 36; ++i) {
                if (BedAuraThirteen.mc.player.inventory.getStackInSlot(i).getItem() != Items.BED) continue;
                BedAuraThirteen.mc.playerController.windowClick(BedAuraThirteen.mc.player.inventoryContainer.windowId, i, 0, ClickType.SWAP, BedAuraThirteen.mc.player);
                break;
            }
        }
    }

    private boolean canPlaceBed(BlockPos pos) {
        return (BedAuraThirteen.mc.world.getBlockState(pos).getBlock() == Blocks.AIR || BedAuraThirteen.mc.world.getBlockState(pos).getBlock() == Blocks.BED) && BedAuraThirteen.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos)).isEmpty();
    }

    private void findClosestTarget() {
        List playerList = BedAuraThirteen.mc.world.playerEntities;
        this.closestTarget = null;
        for (EntityPlayer target : playerList) {
            if (target == BedAuraThirteen.mc.player || OctoHack.friendManager.isFriend(target.getName()) || !BedAuraThirteen.isLiving(target) || target.getHealth() <= 0.0f) continue;
            if (this.closestTarget == null) {
                this.closestTarget = target;
                continue;
            }
            if (!(BedAuraThirteen.mc.player.getDistance(target) < BedAuraThirteen.mc.player.getDistance(this.closestTarget))) continue;
            this.closestTarget = target;
        }
    }

    private void placeBlock(BlockPos pos, EnumFacing side) {
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).addVector(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        BedAuraThirteen.mc.playerController.processRightClickBlock(BedAuraThirteen.mc.player, BedAuraThirteen.mc.world, neighbour, opposite, hitVec, EnumHand.MAIN_HAND);
        BedAuraThirteen.mc.player.swingArm(EnumHand.MAIN_HAND);
    }

    public static boolean isLiving(Entity e) {
        return e instanceof EntityLivingBase;
    }

    @SubscribeEvent
    public void render(RenderWorldLastEvent event) {
        if (this.placeTarget == null || BedAuraThirteen.mc.world == null || this.closestTarget == null) {
            return;
        }
        if (this.placeesp.getValue().booleanValue()) {
            try {
                float posx = this.placeTarget.getX();
                float posy = this.placeTarget.getY();
                float posz = this.placeTarget.getZ();
                RenderUtil.prepare("lines");
                RenderUtil.draw_cube_line(posx, posy, posz, ColorUtil.GenRainbow(), "all");
                if (this.rotVar == 90.0f) {
                    RenderUtil.draw_cube_line(posx - 1.0f, posy, posz, ColorUtil.GenRainbow(), "all");
                }
                if (this.rotVar == 0.0f) {
                    RenderUtil.draw_cube_line(posx, posy, posz + 1.0f, ColorUtil.GenRainbow(), "all");
                }
                if (this.rotVar == -90.0f) {
                    RenderUtil.draw_cube_line(posx + 1.0f, posy, posz, ColorUtil.GenRainbow(), "all");
                }
                if (this.rotVar == 180.0f) {
                    RenderUtil.draw_cube_line(posx, posy, posz - 1.0f, ColorUtil.GenRainbow(), "all");
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
            RenderUtil.release();
        }
    }
}
