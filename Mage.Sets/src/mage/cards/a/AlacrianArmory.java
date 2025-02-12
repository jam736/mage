package mage.cards.a;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.common.continuous.AddCardTypeTargetEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilityControlledEffect;
import mage.abilities.keyword.VigilanceAbility;
import mage.abilities.triggers.BeginningOfCombatTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.filter.FilterPermanent;
import mage.filter.StaticFilters;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.predicate.Predicates;


public final class AlacrianArmory extends CardImpl {

    private static final FilterPermanent filter
            = new FilterControlledPermanent("a Mount or Vehicle you control");

    static {
        filter.add(Predicates.or(
                SubType.MOUNT.getPredicate(),
                SubType.VEHICLE.getPredicate()
        ));
    }

    public AlacrianArmory(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ARTIFACT}, "{3}{W}");

        // Creatures you control get +0/+1 and have reach.
        Ability ability = new SimpleStaticAbility(
                new BoostControlledEffect(0, 1, Duration.WhileOnBattlefield));
        Effect effect = new GainAbilityControlledEffect(VigilanceAbility.getInstance(), 
                Duration.WhileOnBattlefield, StaticFilters.FILTER_PERMANENT_CREATURES);
        effect.setText("and have vigilance.");
        ability.addEffect(effect);
        this.addAbility(ability);

        // At the beginning of combat on your turn, choose up to one target Mount or Vehicle you control. Until end of turn, that permanent becomes saddled if it's a Mount and becomes an artifact creature if it's a Vehicle.
        Ability triggerAbility = new BeginningOfCombatTriggeredAbility(new AlacrianArmoryEffect()
        );
    }

    private AlacrianArmory(final AlacrianArmory card) {
      super(card);
    }

    @Override
    public AlacrianArmory copy() {
      return new AlacrianArmory(this);
    }
}

class AlacrianArmoryEffect extends OneShotEffect {

    AlacrianArmoryEffect() {
        super(Outcome.Benefit);
        this.staticText = "choose up to one target Mount or Vehicle "
            + "you control. Until end of turn, that permanent "
            + "becomes saddled if it's a Mount and becomes an "
            + "artifadct creature if it's a Vehicle.";
    }
}