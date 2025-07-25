package mage.cards.c;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.condition.common.ThresholdCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.common.ActivateIfConditionActivatedAbility;
import mage.abilities.effects.common.continuous.BoostTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AbilityWord;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.target.common.TargetCreaturePermanent;

import java.util.UUID;

/**
 * @author LoneFox
 */
public final class CabalTorturer extends CardImpl {

    public CabalTorturer(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{B}{B}");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.MINION);
        this.power = new MageInt(1);
        this.toughness = new MageInt(1);

        // {B}, {tap}: Target creature gets -1/-1 until end of turn.
        Ability ability = new SimpleActivatedAbility(new BoostTargetEffect(-1, -1, Duration.EndOfTurn), new ManaCostsImpl<>("{B}"));
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetCreaturePermanent());
        this.addAbility(ability);

        // Threshold - {3}{B}{B}, {tap}: Target creature gets -2/-2 until end of turn. Activate this ability only if seven or more cards are in your graveyard.
        ability = new ActivateIfConditionActivatedAbility(
                new BoostTargetEffect(-2, -2, Duration.EndOfTurn),
                new ManaCostsImpl<>("{3}{B}{B}"), ThresholdCondition.instance
        );
        ability.addCost(new TapSourceCost());
        ability.addTarget(new TargetCreaturePermanent());
        ability.setAbilityWord(AbilityWord.THRESHOLD);
        this.addAbility(ability);
    }

    private CabalTorturer(final CabalTorturer card) {
        super(card);
    }

    @Override
    public CabalTorturer copy() {
        return new CabalTorturer(this);
    }
}
