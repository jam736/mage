
package mage.cards.b;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SpellCastControllerTriggeredAbility;
import mage.abilities.condition.Condition;
import mage.abilities.condition.common.SourceHasCounterCondition;
import mage.abilities.costs.common.RemoveCountersSourceCost;
import mage.abilities.effects.common.FlipSourceEffect;
import mage.abilities.effects.common.continuous.BoostTargetEffect;
import mage.abilities.effects.common.counter.AddCountersSourceEffect;
import mage.abilities.keyword.TrampleAbility;
import mage.abilities.triggers.BeginningOfEndStepTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.counters.CounterType;
import mage.filter.StaticFilters;
import mage.game.permanent.token.TokenImpl;
import mage.target.common.TargetCreaturePermanent;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class BudokaPupil extends CardImpl {

    private static final Condition condition = new SourceHasCounterCondition(CounterType.KI, 2);

    public BudokaPupil(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{G}{G}");
        this.subtype.add(SubType.HUMAN, SubType.MONK);

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);
        this.flipCard = true;
        this.flipCardName = "Ichiga, Who Topples Oaks";

        // Whenever you cast a Spirit or Arcane spell, you may put a ki counter on Budoka Pupil.
        this.addAbility(new SpellCastControllerTriggeredAbility(new AddCountersSourceEffect(CounterType.KI.createInstance()), StaticFilters.FILTER_SPELL_SPIRIT_OR_ARCANE, true));

        // At the beginning of the end step, if there are two or more ki counters on Budoka Pupil, you may flip it.
        this.addAbility(new BeginningOfEndStepTriggeredAbility(
                TargetController.NEXT, new FlipSourceEffect(new IchigaWhoTopplesOaks()).setText("flip it"), true, condition
        ));
    }

    private BudokaPupil(final BudokaPupil card) {
        super(card);
    }

    @Override
    public BudokaPupil copy() {
        return new BudokaPupil(this);
    }
}

class IchigaWhoTopplesOaks extends TokenImpl {

    IchigaWhoTopplesOaks() {
        super("Ichiga, Who Topples Oaks", "");
        this.supertype.add(SuperType.LEGENDARY);
        cardType.add(CardType.CREATURE);
        color.setGreen(true);
        subtype.add(SubType.SPIRIT);
        power = new MageInt(4);
        toughness = new MageInt(3);

        // Trample.
        this.addAbility(TrampleAbility.getInstance());

        // Remove a ki counter from Ichiga, Who Topples Oaks: Target creature gets +2/+2 until end of turn.
        Ability ability = new SimpleActivatedAbility(
                new BoostTargetEffect(2, 2, Duration.EndOfTurn),
                new RemoveCountersSourceCost(CounterType.KI.createInstance()));
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);
    }

    private IchigaWhoTopplesOaks(final IchigaWhoTopplesOaks token) {
        super(token);
    }

    public IchigaWhoTopplesOaks copy() {
        return new IchigaWhoTopplesOaks(this);
    }
}
