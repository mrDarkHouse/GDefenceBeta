package com.darkhouse.gdefence.Level.Ability.Tower;


import com.badlogic.gdx.utils.Array;
import com.darkhouse.gdefence.GDefence;
import com.darkhouse.gdefence.Helpers.AssetLoader;
import com.darkhouse.gdefence.Helpers.FontLoader;
import com.darkhouse.gdefence.Level.Ability.Tools.Effect;
import com.darkhouse.gdefence.Level.Mob.Mob;
import com.darkhouse.gdefence.Model.Level.Map;
import com.darkhouse.gdefence.User;

import java.util.concurrent.atomic.AtomicReference;

public class PoisonArrow extends Ability implements Ability.IOnHit {


    private static class Slow extends Effect<Mob> {
        private float percent;
        private float changeSpeed;

        public Slow(float percent, float duration) {
            super(false, true, duration, "slow");
            this.percent = percent;
        }

        public void apply(){
            changeSpeed = owner.getSpeed()*percent;
            owner.changeSpeed(-changeSpeed);
        }

        @Override
        public void dispell() {
            owner.changeSpeed(changeSpeed);
            super.dispell();
        }
    }
    public static class P extends AbilityPrototype {
        private G grader;
        private AtomicReference<Float> slowPercent;
        private AtomicReference<Float> duration;

        public P(float slowPercent, float duration, G grader) {
            super(8, "poisonArrow", grader.gemCap, IOnHit.class);
            this.slowPercent = new AtomicReference<Float>(slowPercent);
            this.duration = new AtomicReference<Float>(duration);
            this.grader = grader;
        }

        @Override
        public Array<Class<? extends AbilityPrototype>> getAbilitiesToSaveOnCraft() {
            return new Array<Class<? extends AbilityPrototype>>();
        }

//        @Override
//        public String getSaveCode() {
//            return null;
//        }

        @Override
        public AbilityPrototype copy() {
            AssetLoader l = GDefence.getInstance().assetLoader;
            P p = new P(slowPercent.get(), duration.get(), grader);
            p.gemBoost[0] = new BoostFloat(p.slowPercent, grader.slowPercentUp, l.getWord("poisonArrowGrade1"),
                    true, BoostFloat.FloatGradeFieldType.PERCENT);
            p.gemBoost[1] = new BoostFloat(p.duration, grader.durationUp, l.getWord("poisonArrowGrade2"),
                    true, BoostFloat.FloatGradeFieldType.TIME);
            return p;
        }

        @Override
        public Ability getAbility() {
            return new PoisonArrow(this);
        }

        @Override
        public String getTooltip() {
            AssetLoader l = GDefence.getInstance().assetLoader;
            return l.getWord("poisonArrowTooltip1") + " " + FontLoader.colorString(slowPercent.get()*100 + "%", User.GEM_TYPE.BLACK) + " " +
                    l.getWord("poisonArrowTooltip2") + " " + FontLoader.colorString(duration.get().toString(), User.GEM_TYPE.GREEN) + " " +
                    l.getWord("poisonArrowTooltip3");
        }

    }
    public static class G extends AbilityGrader{
        private float slowPercentUp;
        private float durationUp;

        public G(float slowPercentUp, float durationUp, int[] gemCap) {
            super(gemCap);
            this.slowPercentUp = slowPercentUp;
            this.durationUp = durationUp;
        }
    }

//    private Slow slow;

    private float percent;
    private float duration;

    public PoisonArrow(P prototype) {
        this.percent = prototype.slowPercent.get();
        this.duration = prototype.duration.get();
    }

//    @Override
//    public void use(Mob target) {
//        if(target != null) {
////            Slow w = new Slow(0.3f, 3f);
////            w.setOwner(target);
//            target.addEffect(new Slow(0.3f, 3f).setOwner(target));
//        }
//    }

    @Override
    public int getDmg(Mob target, int startDmg) {
        if(target != null) {
            target.addEffect(new Slow(percent, duration).setOwner(target));
        }
        return startDmg;
    }

    @Override
    protected void init(Map map) {

    }

}
