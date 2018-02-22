package com.darkhouse.gdefence.Level.Ability.Tower;


import com.badlogic.gdx.utils.Array;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Helpers.AssetLoader;
import com.darkhouse.gdefence.Helpers.FontLoader;
import com.darkhouse.gdefence.Level.Ability.Tools.DamageType;
import com.darkhouse.gdefence.Level.Mob.Mob;
import com.darkhouse.gdefence.Level.Tower.Projectile;
import com.darkhouse.gdefence.Model.Level.Map;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.atomic.AtomicReference;

public class Splash extends Ability implements Ability.IAfterHit{

    public static class P extends AbilityPrototype {
        private G g;
        private AtomicReference<Float> aoeDmg;
        private AtomicReference<Integer> aoe;

        public P(int aoe, float aoeDmg, G grader) {
            super(10, "splash", grader.gemCap);
            this.aoe = new AtomicReference<Integer>(aoe);
            this.aoeDmg = new AtomicReference<Float>(aoeDmg);
            this.g = grader;
        }

        @Override
        public Array<Class<? extends AbilityPrototype>> getAbilitiesToSaveOnCraft() {
            Array<Class<? extends AbilityPrototype>> a = new Array<Class<? extends AbilityPrototype>>();
            a.add(Splash.P.class);
            return a;
        }

//        @Override
//        public String getSaveCode() {
//            return super.getSaveCode() + "z" + aoe + ";" + aoeDmg + ";" + g.aoeUp + ";" + g.aoeDmgUp;
//        }

        @Override
        public AbilityPrototype copy() {
            AssetLoader l = GDefence.getInstance().assetLoader;
            P p = new P(aoe.get(), aoeDmg.get(), g);
            p.gemBoost[0] = new BoostInteger(p.aoe, g.aoeUp, l.getWord("splashGrade1"),
                    true, BoostInteger.IntegerGradeFieldType.NONE);
            p.gemBoost[1] = new BoostFloat(p.aoeDmg, g.aoeDmgUp, l.getWord("splashGrade2"),
                    true, BoostFloat.FloatGradeFieldType.PERCENT);
            return p;
        }

        @Override
        public Ability getAbility() {
            return new Splash(this);
        }

        @Override
        public String getTooltip() {
            AssetLoader l = GDefence.getInstance().assetLoader;
            return l.getWord("splashTooltip1") + " " + FontLoader.colorString(new BigDecimal(aoeDmg.get() * 100).setScale(2, RoundingMode.HALF_UP).floatValue() + "%", 1) + " " +
                    l.getWord("splashTooltip2") + System.getProperty("line.separator") +
                    l.getWord("splashTooltip3") + " " + FontLoader.colorString(aoe.get().toString(), 0) + " " +
                    l.getWord("splashTooltip4");
        }

    }
    public static class G extends AbilityGrader{
        private int aoeUp;
        private float aoeDmgUp;

        public G(int aoeUp, float aoeDmgUp, int[] gemCap) {
            super(gemCap);
            this.aoeUp = aoeUp;
            this.aoeDmgUp = aoeDmgUp;
        }
    }

    private float aoeDmg;
    private int aoe;

    public Splash(P prototype) {
        this.aoe = prototype.aoe.get();
        this.aoeDmg = prototype.aoeDmg.get();
    }

    @Override
    public void hit(Mob target, int dmg, Projectile hittingProjectile) {
//        for (Mob m: Wave.mobs){
//
//        }
        for (Mob m:Map.getMobsInRange(target, aoe)){
            if(m != target) {//no splash damage to main target
//                owner.hitTarget(m, (int) (dmg * aoeDmg));
                float damaged = m.hit(dmg*aoeDmg, DamageType.PhysicNoContact, owner);
//                System.out.println(m + " " + damaged);
                owner.addExp(owner.getExpFromDmg(damaged));
            }
        }
    }

    @Override
    protected void init() {

    }
}
