
package mage.cards.v;

import mage.Mana;
import mage.abilities.Ability;
import mage.abilities.TriggeredAbility;
import mage.abilities.common.ActivateAsSorceryActivatedAbility;
import mage.abilities.triggers.BeginningOfFirstMainTriggeredAbility;
import mage.abilities.condition.common.SourceHasCounterCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.decorator.ConditionalInterveningIfTriggeredAbility;
import mage.abilities.dynamicvalue.common.GetXValue;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.counters.CounterType;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class VentifactBottle extends CardImpl {

    public VentifactBottle(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{3}");

        // {X}{1}, {tap}: Put X charge counters on Ventifact Bottle. Activate this ability only any time you could cast a sorcery.
        Ability ability = new ActivateAsSorceryActivatedAbility(Zone.BATTLEFIELD,
                new AddCountersSourceEffect(CounterType.CHARGE.createInstance(), GetXValue.instance, true),
                new ManaCostsImpl<>("{X}{1}"));
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);
        // At the beginning of your precombat main phase, if Ventifact Bottle has a charge counter on it, tap it and remove all charge counters from it. Add {C} for each charge counter removed this way.
        TriggeredAbility ability2 = new BeginningOfFirstMainTriggeredAbility(new VentifactBottleEffect());
        this.addAbility(new ConditionalInterveningIfTriggeredAbility(ability2,
                new SourceHasCounterCondition(CounterType.CHARGE, 1, Integer.MAX_VALUE),
                "At the beginning of your first main phase, "
                        + "if {this} has a charge counter on it, tap it and remove all charge counters from it. "
                        + "Add {C} for each charge counter removed this way."));
    }

    private VentifactBottle(final VentifactBottle card) {
        super(card);
    }

    @Override
    public VentifactBottle copy() {
        return new VentifactBottle(this);
    }
}

class VentifactBottleEffect extends OneShotEffect {

    VentifactBottleEffect() {
        super(Outcome.Benefit);
    }

    private VentifactBottleEffect(final VentifactBottleEffect effect) {
        super(effect);
    }

    @Override
    public VentifactBottleEffect copy() {
        return new VentifactBottleEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent sourcePermanent = game.getPermanent(source.getSourceId());
        Player player = game.getPlayer(source.getControllerId());
        if (sourcePermanent != null && player != null) {
            int amountRemoved = sourcePermanent.removeAllCounters(CounterType.CHARGE.getName(), source, game);
            sourcePermanent.tap(source, game);
            Mana mana = new Mana();
            mana.setColorless(amountRemoved);
            player.getManaPool().addMana(mana, game, source);
            return true;
        }
        return false;
    }
}
