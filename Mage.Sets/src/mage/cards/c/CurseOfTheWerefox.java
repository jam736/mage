package mage.cards.c;

import mage.abilities.Ability;
import mage.abilities.common.delayed.ReflexiveTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.RoleType;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.TargetPermanent;
import mage.target.common.TargetControlledCreaturePermanent;
import mage.target.targetpointer.FixedTarget;

import java.util.UUID;

/**
 * @author Susucr
 */
public final class CurseOfTheWerefox extends CardImpl {

    public CurseOfTheWerefox(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{2}{G}");

        // Create a Monster Role token attached to target creature you control. When you do, that creature fights up to one target creature you don't control.
        this.getSpellAbility().addEffect(new CurseOfTheWerefoxEffect());
        this.getSpellAbility().addTarget(new TargetControlledCreaturePermanent());
    }

    private CurseOfTheWerefox(final CurseOfTheWerefox card) {
        super(card);
    }

    @Override
    public CurseOfTheWerefox copy() {
        return new CurseOfTheWerefox(this);
    }
}

class CurseOfTheWerefoxEffect extends OneShotEffect {

    CurseOfTheWerefoxEffect() {
        super(Outcome.Benefit);
        staticText = "create a Monster Role token attached to target creature you control. "
                + "When you do, that creature fights up to one target creature you don't control";
    }

    private CurseOfTheWerefoxEffect(final CurseOfTheWerefoxEffect effect) {
        super(effect);
    }

    @Override
    public CurseOfTheWerefoxEffect copy() {
        return new CurseOfTheWerefoxEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent target = game.getPermanent(source.getFirstTarget());
        if (target == null || RoleType.MONSTER.createToken(target, game, source).getLastAddedTokenIds().isEmpty()) {
            return false;
        }

        ReflexiveTriggeredAbility ability = new ReflexiveTriggeredAbility(
                new CurseOfTheWerefoxFightEffect().setTargetPointer(new FixedTarget(target.getId(), game)),
                false, "that creature fights up to one target creature you don't control"
        );
        ability.addTarget(new TargetPermanent(0, 1, StaticFilters.FILTER_CREATURE_YOU_DONT_CONTROL));
        game.fireReflexiveTriggeredAbility(ability, source);
        return true;
    }
}


class CurseOfTheWerefoxFightEffect extends OneShotEffect {

    CurseOfTheWerefoxFightEffect() {
        super(Outcome.Damage);
    }

    private CurseOfTheWerefoxFightEffect(final CurseOfTheWerefoxFightEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent triggeredCreature = game.getPermanent(this.getTargetPointer().getFirst(game, source));
        Permanent target = game.getPermanent(source.getFirstTarget());
        if (triggeredCreature != null
                && target != null
                && triggeredCreature.isCreature(game)
                && target.isCreature(game)) {
            return triggeredCreature.fight(target, source, game);
        }
        return false;
    }

    @Override
    public CurseOfTheWerefoxFightEffect copy() {
        return new CurseOfTheWerefoxFightEffect(this);
    }
}
