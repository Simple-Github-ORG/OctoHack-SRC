package me.primooctopus33.octohack.client.modules.render;

import java.awt.Color;
import java.util.ArrayList;
import me.primooctopus33.octohack.client.modules.Module;
import me.primooctopus33.octohack.client.setting.Setting;
import me.primooctopus33.octohack.util.HoleUtil;
import me.primooctopus33.octohack.util.RenderUtil;
import me.primooctopus33.octohack.util.WorldUtils;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HoleESP
extends Module {
    public static Setting<Page> page;
    public static Setting<Integer> validHoleHeight;
    public static Setting<Mode> mode;
    public static Setting<DoubleMode> doubleMode;
    public static Setting<QuadMode> quadMode;
    public static Setting<Integer> range;
    public static Setting<Integer> oRed;
    public static Setting<Integer> oGreen;
    public static Setting<Integer> oBlue;
    public static Setting<Integer> oAlpha;
    public static Setting<Integer> bRed;
    public static Setting<Integer> bGreen;
    public static Setting<Integer> bBlue;
    public static Setting<Integer> bAlpha;
    public static Setting<Boolean> doubles;
    public static Setting<Integer> dRed;
    public static Setting<Integer> dGreen;
    public static Setting<Integer> dBlue;
    public static Setting<Integer> dAlpha;
    public static Setting<Boolean> quads;
    public static Setting<Integer> qRed;
    public static Setting<Integer> qGreen;
    public static Setting<Integer> qBlue;
    public static Setting<Integer> qAlpha;
    public static Setting<Integer> oRedLine;
    public static Setting<Integer> oGreenLine;
    public static Setting<Integer> oBlueLine;
    public static Setting<Integer> oAlphaLine;
    public static Setting<Integer> bRedLine;
    public static Setting<Integer> bGreenLine;
    public static Setting<Integer> bBlueLine;
    public static Setting<Integer> bAlphaLine;
    public static Setting<Boolean> doublesLine;
    public static Setting<Integer> dRedLine;
    public static Setting<Integer> dGreenLine;
    public static Setting<Integer> dBlueLine;
    public static Setting<Integer> dAlphaLine;
    public static Setting<Boolean> quadsLine;
    public static Setting<Integer> qRedLine;
    public static Setting<Integer> qGreenLine;
    public static Setting<Integer> qBlueLine;
    public static Setting<Integer> qAlphaLine;

    public HoleESP() {
        super("HoleESP", "Shows safe holes for crystal pvp at bedrock", Module.Category.RENDER, true, false, false);
        page = this.register(new Setting<Page>("Page", Page.Fill));
        range = this.register(new Setting<Object>("Range", Integer.valueOf(5), Integer.valueOf(1), Integer.valueOf(20), v -> page.getValue() == Page.Fill));
        mode = this.register(new Setting<Mode>("Mode", Mode.Gradient1));
        doubleMode = this.register(new Setting<DoubleMode>("Double Mode", DoubleMode.Static));
        quadMode = this.register(new Setting<QuadMode>("Quad Mode", QuadMode.Dynamic));
        validHoleHeight = this.register(new Setting<Object>("Valid Hole Height", Integer.valueOf(2), Integer.valueOf(1), Integer.valueOf(5), v -> page.getValue() == Page.Fill));
        oRed = this.register(new Setting<Object>("Obsidian Red", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        oGreen = this.register(new Setting<Object>("Obsidian Green", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        oBlue = this.register(new Setting<Object>("Obsidian Blue", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        oAlpha = this.register(new Setting<Object>("Obsidian Alpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        bRed = this.register(new Setting<Object>("Bedrock Red", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        bGreen = this.register(new Setting<Object>("Bedrock Green", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        bBlue = this.register(new Setting<Object>("Bedrock Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        bAlpha = this.register(new Setting<Object>("Bedrock Alpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        doubles = this.register(new Setting<Object>("Double Holes", Boolean.valueOf(true), v -> page.getValue() == Page.Fill));
        dRed = this.register(new Setting<Object>("Double Red", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        dGreen = this.register(new Setting<Object>("Double Green", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        dBlue = this.register(new Setting<Object>("Double Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        dAlpha = this.register(new Setting<Object>("Double Alpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        quads = this.register(new Setting<Object>("Quad Holes", Boolean.valueOf(true), v -> page.getValue() == Page.Fill));
        qRed = this.register(new Setting<Object>("Quad Red", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        qGreen = this.register(new Setting<Object>("Quad Green", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        qBlue = this.register(new Setting<Object>("Quad Blue", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        qAlpha = this.register(new Setting<Object>("Quad Alpha", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Fill));
        oRedLine = this.register(new Setting<Object>("Obsidian Red Line", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        oGreenLine = this.register(new Setting<Object>("Obsidian Green Line", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        oBlueLine = this.register(new Setting<Object>("Obsidian Blue Line", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        oAlphaLine = this.register(new Setting<Object>("Obsidian Alpha Line", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        bRedLine = this.register(new Setting<Object>("Bedrock Red Line", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        bGreenLine = this.register(new Setting<Object>("Bedrock Green Line", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        bBlueLine = this.register(new Setting<Object>("Bedrock Blue Line", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        bAlphaLine = this.register(new Setting<Object>("Bedrock Alpha Line", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        doublesLine = this.register(new Setting<Object>("Double Holes Line", Boolean.valueOf(true), v -> page.getValue() == Page.Line));
        dRedLine = this.register(new Setting<Object>("Double Red Line", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        dGreenLine = this.register(new Setting<Object>("Double Green Line", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        dBlueLine = this.register(new Setting<Object>("Double Blue Line", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        dAlphaLine = this.register(new Setting<Object>("Double Alpha Line", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        quadsLine = this.register(new Setting<Object>("Quad Holes Line", Boolean.valueOf(true), v -> page.getValue() == Page.Line));
        qRedLine = this.register(new Setting<Object>("Quad Red Line", Integer.valueOf(30), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        qGreenLine = this.register(new Setting<Object>("Quad Green Line", Integer.valueOf(167), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        qBlueLine = this.register(new Setting<Object>("Quad Blue Line", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
        qAlphaLine = this.register(new Setting<Object>("Quad Alpha Line", Integer.valueOf(60), Integer.valueOf(0), Integer.valueOf(255), v -> page.getValue() == Page.Line));
    }

    public Color getObsidianColor() {
        return new Color(oRed.getValue(), oGreen.getValue(), oBlue.getValue(), oAlpha.getValue());
    }

    public Color getObsidianNoAlphaColor() {
        return new Color(oRed.getValue(), oGreen.getValue(), oBlue.getValue(), 0);
    }

    public Color getBedrockColor() {
        return new Color(bRed.getValue(), bGreen.getValue(), bBlue.getValue(), bAlpha.getValue());
    }

    public Color getBedrockNoAlphaColor() {
        return new Color(bRed.getValue(), bGreen.getValue(), bBlue.getValue(), 0);
    }

    public Color getDoubleColor() {
        return new Color(dRed.getValue(), dGreen.getValue(), dBlue.getValue(), dAlpha.getValue());
    }

    public Color getDoubleNoAlphaColor() {
        return new Color(dRed.getValue(), dGreen.getValue(), dBlue.getValue(), 0);
    }

    public Color getQuadColor() {
        return new Color(qRed.getValue(), qGreen.getValue(), qBlue.getValue(), qAlpha.getValue());
    }

    public Color getQuadNoAlphaColor() {
        return new Color(qRed.getValue(), qGreen.getValue(), qBlue.getValue(), qAlpha.getValue());
    }

    public Color getLineObsidianColor() {
        return new Color(oRedLine.getValue(), oGreenLine.getValue(), oBlueLine.getValue(), oAlphaLine.getValue());
    }

    public Color getLineObsidianNoAlphaColor() {
        return new Color(oRedLine.getValue(), oGreenLine.getValue(), oBlueLine.getValue(), 0);
    }

    public Color getLineBedrockColor() {
        return new Color(bRedLine.getValue(), bGreenLine.getValue(), bBlueLine.getValue(), bAlphaLine.getValue());
    }

    public Color getLineBedrockNoAlphaColor() {
        return new Color(bRedLine.getValue(), bGreenLine.getValue(), bBlueLine.getValue(), 0);
    }

    public Color getLineDoubleColor() {
        return new Color(dRedLine.getValue(), dGreenLine.getValue(), dBlueLine.getValue(), dAlphaLine.getValue());
    }

    public Color getLineDoubleNoAlphaColor() {
        return new Color(dRedLine.getValue(), dGreenLine.getValue(), dBlueLine.getValue(), 0);
    }

    public Color getLineQuadColor() {
        return new Color(qRedLine.getValue(), qGreenLine.getValue(), qBlueLine.getValue(), qAlphaLine.getValue());
    }

    public Color getLineQuadNoAlphaColor() {
        return new Color(qRedLine.getValue(), qGreenLine.getValue(), qBlueLine.getValue(), qAlphaLine.getValue());
    }

    @SubscribeEvent
    public void onRender3D(RenderWorldLastEvent event) {
        ArrayList<HoleUtil.Hole> holesList = HoleUtil.holes(range.getValue().intValue(), validHoleHeight.getValue());
        for (HoleUtil.Hole holes : holesList) {
            AxisAlignedBB bb;
            if (holes instanceof HoleUtil.SingleHole && WorldUtils.empty.contains(WorldUtils.getBlock(((HoleUtil.SingleHole)holes).pos))) {
                HoleUtil.SingleHole pos = (HoleUtil.SingleHole)holes;
                bb = new AxisAlignedBB(pos.pos);
                if (holes.mat == HoleUtil.material.OBSIDIAN) {
                    if (mode.getValue() == Mode.Full) {
                        RenderUtil.renderBB(7, bb, this.getObsidianColor(), this.getObsidianColor());
                        RenderUtil.renderBB(3, bb, this.getLineObsidianColor(), this.getLineObsidianColor());
                    }
                    if (mode.getValue() == Mode.Gradient1) {
                        RenderUtil.renderBB(7, bb, this.getObsidianColor(), this.getObsidianNoAlphaColor());
                        RenderUtil.renderBB(3, bb, this.getLineObsidianColor(), this.getLineObsidianNoAlphaColor());
                    }
                    if (mode.getValue() == Mode.Gradient2) {
                        RenderUtil.renderBB(7, bb, this.getObsidianNoAlphaColor(), this.getObsidianColor());
                        RenderUtil.renderBB(3, bb, this.getLineObsidianNoAlphaColor(), this.getLineObsidianColor());
                    }
                }
                if (holes.mat == HoleUtil.material.BEDROCK) {
                    if (mode.getValue() == Mode.Full) {
                        RenderUtil.renderBB(7, bb, this.getBedrockColor(), this.getBedrockColor());
                        RenderUtil.renderBB(3, bb, this.getLineBedrockColor(), this.getLineBedrockColor());
                    }
                    if (mode.getValue() == Mode.Gradient1) {
                        RenderUtil.renderBB(7, bb, this.getBedrockColor(), this.getBedrockNoAlphaColor());
                        RenderUtil.renderBB(3, bb, this.getLineBedrockColor(), this.getLineBedrockNoAlphaColor());
                    }
                    if (mode.getValue() == Mode.Gradient2) {
                        RenderUtil.renderBB(7, bb, this.getBedrockNoAlphaColor(), this.getBedrockColor());
                        RenderUtil.renderBB(3, bb, this.getLineBedrockNoAlphaColor(), this.getLineBedrockColor());
                    }
                }
            }
            if (holes instanceof HoleUtil.DoubleHole && doubles.getValue().booleanValue()) {
                HoleUtil.DoubleHole doublePos = (HoleUtil.DoubleHole)holes;
                bb = new AxisAlignedBB(doublePos.pos);
                bb = bb.expand(doublePos.dir.getFrontOffsetX(), doublePos.dir.getFrontOffsetY(), doublePos.dir.getFrontOffsetZ());
                if (this.getDist(doublePos.pos) && WorldUtils.empty.contains(WorldUtils.getBlock(doublePos.pos))) {
                    if (doubleMode.getValue() == DoubleMode.Dynamic && holes.mat == HoleUtil.material.BEDROCK) {
                        if (mode.getValue() == Mode.Full) {
                            RenderUtil.renderBB(7, bb, this.getBedrockColor(), this.getBedrockColor());
                            RenderUtil.renderBB(3, bb, this.getLineBedrockColor(), this.getLineBedrockColor());
                        }
                        if (mode.getValue() == Mode.Gradient1) {
                            RenderUtil.renderBB(7, bb, this.getBedrockColor(), this.getBedrockNoAlphaColor());
                            RenderUtil.renderBB(3, bb, this.getLineBedrockColor(), this.getLineBedrockNoAlphaColor());
                        }
                        if (mode.getValue() == Mode.Gradient2) {
                            RenderUtil.renderBB(7, bb, this.getBedrockNoAlphaColor(), this.getBedrockColor());
                            RenderUtil.renderBB(3, bb, this.getLineBedrockNoAlphaColor(), this.getLineBedrockColor());
                        }
                    }
                    if (doubleMode.getValue() == DoubleMode.Static) {
                        if (mode.getValue() == Mode.Full) {
                            RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleColor());
                            RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleColor());
                        }
                        if (mode.getValue() == Mode.Gradient1) {
                            RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleNoAlphaColor());
                            RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleNoAlphaColor());
                        }
                        if (mode.getValue() == Mode.Gradient2) {
                            RenderUtil.renderBB(7, bb, this.getDoubleNoAlphaColor(), this.getDoubleColor());
                            RenderUtil.renderBB(3, bb, this.getLineDoubleNoAlphaColor(), this.getLineDoubleColor());
                        }
                    }
                }
                if (this.getDist(doublePos.pos) && WorldUtils.empty.contains(WorldUtils.getBlock(doublePos.pos1))) {
                    if (doubleMode.getValue() == DoubleMode.Dynamic && holes.mat == HoleUtil.material.OBSIDIAN) {
                        if (mode.getValue() == Mode.Full) {
                            RenderUtil.renderBB(7, bb, this.getObsidianColor(), this.getObsidianColor());
                            RenderUtil.renderBB(3, bb, this.getLineObsidianColor(), this.getLineObsidianColor());
                        }
                        if (mode.getValue() == Mode.Gradient1) {
                            RenderUtil.renderBB(7, bb, this.getObsidianColor(), this.getObsidianNoAlphaColor());
                            RenderUtil.renderBB(3, bb, this.getLineObsidianColor(), this.getLineObsidianNoAlphaColor());
                        }
                        if (mode.getValue() == Mode.Gradient2) {
                            RenderUtil.renderBB(7, bb, this.getObsidianNoAlphaColor(), this.getObsidianColor());
                            RenderUtil.renderBB(3, bb, this.getLineObsidianNoAlphaColor(), this.getLineObsidianColor());
                        }
                    }
                    if (doubleMode.getValue() == DoubleMode.Static) {
                        if (mode.getValue() == Mode.Full) {
                            RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleColor());
                            RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleColor());
                        }
                        if (mode.getValue() == Mode.Gradient1) {
                            RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleNoAlphaColor());
                            RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleNoAlphaColor());
                        }
                        if (mode.getValue() == Mode.Gradient2) {
                            RenderUtil.renderBB(7, bb, this.getDoubleNoAlphaColor(), this.getDoubleColor());
                            RenderUtil.renderBB(3, bb, this.getLineDoubleNoAlphaColor(), this.getLineDoubleColor());
                        }
                    }
                }
            }
            if (!(holes instanceof HoleUtil.QuadHole) || !quads.getValue().booleanValue()) continue;
            HoleUtil.QuadHole quadPos = (HoleUtil.QuadHole)holes;
            bb = new AxisAlignedBB(quadPos.pos);
            bb = bb.expand(quadPos.dir.getFrontOffsetX(), quadPos.dir.getFrontOffsetY(), quadPos.dir.getFrontOffsetZ());
            if (this.getDist(quadPos.pos) && WorldUtils.empty.contains(WorldUtils.getBlock(quadPos.pos))) {
                if (quadMode.getValue() == QuadMode.Dynamic && holes.mat == HoleUtil.material.BEDROCK) {
                    if (mode.getValue() == Mode.Full) {
                        RenderUtil.renderBB(7, bb, this.getQuadColor(), this.getQuadColor());
                        RenderUtil.renderBB(3, bb, this.getLineQuadColor(), this.getLineQuadColor());
                    }
                    if (mode.getValue() == Mode.Gradient1) {
                        RenderUtil.renderBB(7, bb, this.getQuadColor(), this.getQuadNoAlphaColor());
                        RenderUtil.renderBB(3, bb, this.getLineQuadColor(), this.getLineBedrockNoAlphaColor());
                    }
                    if (mode.getValue() == Mode.Gradient2) {
                        RenderUtil.renderBB(7, bb, this.getQuadNoAlphaColor(), this.getQuadColor());
                        RenderUtil.renderBB(3, bb, this.getLineBedrockNoAlphaColor(), this.getLineQuadColor());
                    }
                }
                if (quadMode.getValue() == QuadMode.Static) {
                    if (mode.getValue() == Mode.Full) {
                        RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleColor());
                    }
                    if (mode.getValue() == Mode.Gradient1) {
                        RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleNoAlphaColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleNoAlphaColor());
                    }
                    if (mode.getValue() == Mode.Gradient2) {
                        RenderUtil.renderBB(7, bb, this.getDoubleNoAlphaColor(), this.getDoubleColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleNoAlphaColor(), this.getLineDoubleColor());
                    }
                }
            }
            if (this.getDist(quadPos.pos) && WorldUtils.empty.contains(WorldUtils.getBlock(quadPos.pos1))) {
                if (quadMode.getValue() == QuadMode.Dynamic && holes.mat == HoleUtil.material.BEDROCK) {
                    if (mode.getValue() == Mode.Full) {
                        RenderUtil.renderBB(7, bb, this.getQuadColor(), this.getQuadColor());
                        RenderUtil.renderBB(3, bb, this.getLineQuadColor(), this.getLineQuadColor());
                    }
                    if (mode.getValue() == Mode.Gradient1) {
                        RenderUtil.renderBB(7, bb, this.getQuadColor(), this.getQuadNoAlphaColor());
                        RenderUtil.renderBB(3, bb, this.getLineQuadColor(), this.getLineBedrockNoAlphaColor());
                    }
                    if (mode.getValue() == Mode.Gradient2) {
                        RenderUtil.renderBB(7, bb, this.getQuadNoAlphaColor(), this.getQuadColor());
                        RenderUtil.renderBB(3, bb, this.getLineBedrockNoAlphaColor(), this.getLineQuadColor());
                    }
                }
                if (quadMode.getValue() == QuadMode.Static) {
                    if (mode.getValue() == Mode.Full) {
                        RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleColor());
                    }
                    if (mode.getValue() == Mode.Gradient1) {
                        RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleNoAlphaColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleNoAlphaColor());
                    }
                    if (mode.getValue() == Mode.Gradient2) {
                        RenderUtil.renderBB(7, bb, this.getDoubleNoAlphaColor(), this.getDoubleColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleNoAlphaColor(), this.getLineDoubleColor());
                    }
                }
            }
            if (this.getDist(quadPos.pos) && WorldUtils.empty.contains(WorldUtils.getBlock(quadPos.pos2))) {
                if (quadMode.getValue() == QuadMode.Dynamic && holes.mat == HoleUtil.material.BEDROCK) {
                    if (mode.getValue() == Mode.Full) {
                        RenderUtil.renderBB(7, bb, this.getQuadColor(), this.getQuadColor());
                        RenderUtil.renderBB(3, bb, this.getLineQuadColor(), this.getLineQuadColor());
                    }
                    if (mode.getValue() == Mode.Gradient1) {
                        RenderUtil.renderBB(7, bb, this.getQuadColor(), this.getQuadNoAlphaColor());
                        RenderUtil.renderBB(3, bb, this.getLineQuadColor(), this.getLineBedrockNoAlphaColor());
                    }
                    if (mode.getValue() == Mode.Gradient2) {
                        RenderUtil.renderBB(7, bb, this.getQuadNoAlphaColor(), this.getQuadColor());
                        RenderUtil.renderBB(3, bb, this.getLineBedrockNoAlphaColor(), this.getLineQuadColor());
                    }
                }
                if (quadMode.getValue() == QuadMode.Static) {
                    if (mode.getValue() == Mode.Full) {
                        RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleColor());
                    }
                    if (mode.getValue() == Mode.Gradient1) {
                        RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleNoAlphaColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleNoAlphaColor());
                    }
                    if (mode.getValue() == Mode.Gradient2) {
                        RenderUtil.renderBB(7, bb, this.getDoubleNoAlphaColor(), this.getDoubleColor());
                        RenderUtil.renderBB(3, bb, this.getLineDoubleNoAlphaColor(), this.getLineDoubleColor());
                    }
                }
            }
            if (!this.getDist(quadPos.pos) || !WorldUtils.empty.contains(WorldUtils.getBlock(quadPos.pos3))) continue;
            if (quadMode.getValue() == QuadMode.Dynamic && holes.mat == HoleUtil.material.BEDROCK) {
                if (mode.getValue() == Mode.Full) {
                    RenderUtil.renderBB(7, bb, this.getQuadColor(), this.getQuadColor());
                    RenderUtil.renderBB(3, bb, this.getLineQuadColor(), this.getLineQuadColor());
                }
                if (mode.getValue() == Mode.Gradient1) {
                    RenderUtil.renderBB(7, bb, this.getQuadColor(), this.getQuadNoAlphaColor());
                    RenderUtil.renderBB(3, bb, this.getLineQuadColor(), this.getLineBedrockNoAlphaColor());
                }
                if (mode.getValue() == Mode.Gradient2) {
                    RenderUtil.renderBB(7, bb, this.getQuadNoAlphaColor(), this.getQuadColor());
                    RenderUtil.renderBB(3, bb, this.getLineBedrockNoAlphaColor(), this.getLineQuadColor());
                }
            }
            if (quadMode.getValue() != QuadMode.Static) continue;
            if (mode.getValue() == Mode.Full) {
                RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleColor());
                RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleColor());
            }
            if (mode.getValue() == Mode.Gradient1) {
                RenderUtil.renderBB(7, bb, this.getDoubleColor(), this.getDoubleNoAlphaColor());
                RenderUtil.renderBB(3, bb, this.getLineDoubleColor(), this.getLineDoubleNoAlphaColor());
            }
            if (mode.getValue() != Mode.Gradient2) continue;
            RenderUtil.renderBB(7, bb, this.getDoubleNoAlphaColor(), this.getDoubleColor());
            RenderUtil.renderBB(3, bb, this.getLineDoubleNoAlphaColor(), this.getLineDoubleColor());
        }
    }

    private boolean getDist(BlockPos pos) {
        if (HoleESP.nullCheck() || pos == null) {
            return false;
        }
        return pos.add(0.5, 0.5, 0.5).distanceSq(HoleESP.mc.player.posX, HoleESP.mc.player.posY + (double)HoleESP.mc.player.eyeHeight, HoleESP.mc.player.posZ) < Math.pow(range.getValue().intValue(), 2.0);
    }

    public static enum Page {
        Fill,
        Line;

    }

    public static enum QuadMode {
        Static,
        Dynamic;

    }

    public static enum DoubleMode {
        Static,
        Dynamic;

    }

    public static enum Mode {
        Gradient1,
        Gradient2,
        Full;

    }
}
