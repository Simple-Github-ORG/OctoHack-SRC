package me.primooctopus33.octohack.client.modules.combat;

import me.primooctopus33.octohack.OctoHack;
import me.primooctopus33.octohack.client.modules.combat.AutoCrystal;

class AutoCrystal$RAutoCrystalRewrite
implements Runnable {
    private static AutoCrystal$RAutoCrystalRewrite instance;
    private AutoCrystal AutoCrystalRewrite;

    private AutoCrystal$RAutoCrystalRewrite() {
    }

    public static AutoCrystal$RAutoCrystalRewrite getInstance(AutoCrystal AutoCrystalRewrite) {
        if (instance == null) {
            instance = new AutoCrystal$RAutoCrystalRewrite();
            AutoCrystal$RAutoCrystalRewrite.instance.AutoCrystalRewrite = AutoCrystalRewrite;
        }
        return instance;
    }

    @Override
    public void run() {
        if (this.AutoCrystalRewrite.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
            while (this.AutoCrystalRewrite.isOn() && this.AutoCrystalRewrite.threadMode.getValue() == AutoCrystal.ThreadMode.WHILE) {
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
        } else if (this.AutoCrystalRewrite.threadMode.getValue() != AutoCrystal.ThreadMode.NONE && this.AutoCrystalRewrite.isOn()) {
            while (OctoHack.eventManager.ticksOngoing()) {
            }
            this.AutoCrystalRewrite.threadOngoing.set(true);
            OctoHack.safetyManager.doSafetyCheck();
            this.AutoCrystalRewrite.doAutoCrystalRewrite();
            this.AutoCrystalRewrite.threadOngoing.set(false);
        }
    }
}
