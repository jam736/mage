package mage.cards.a;

import java.util.UUID;
import mage.abilities.Ability;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.effects.Effect;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.continuous.AddCardTypeTargetEffect;
import mage.abilities.effects.common.continuous.BoostControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilityControlledEffect;
import mage.abilities.effects.common.continuous.GainAbilityTargetEffect;
import mage.abilities.keyword.SaddleAbility;
import mage.abilities.keyword.VigilanceAbility;
import mage.abilities.triggers.BeginningOfCombatTriggeredAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.filter.FilterPermanent;
import mage.filter.StaticFilters;
import mage.filter.common.FilterControlledPermanent;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.Predicates;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.target.common.TargetControlledPermanent;


public final class AlacrianArmory extends CardImpl {

    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("Mounts and Vehicles");

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
        Ability triggerAbility = new BeginningOfCombatTriggeredAbility(
                new AlacrianArmoryEffect()
        );
        triggerAbility.addTarget(new TargetControlledPermanent(filter));
        this.addAbility(triggerAbility);
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
            + "artifact creature if it's a Vehicle.";
    }

    private AlacrianArmoryEffect(final
    AlacrianArmoryEffect effect) {
        super(effect);
    }

    @Override
    public AlacrianArmoryEffect copy() {
        return new AlacrianArmoryEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent mountOrVehicle = game.getPermanent(source.getFirstTarget());
        if (mountOrVehicle == null) {
            return false;
        }
        if (mountOrVehicle.hasSubtype(SubType.MOUNT, game)) {
            game.addEffect(new SaddleAbility(0));
        }

        if (mountOrVehicle.hasSubtype(SubType.VEHICLE, game)) {
            game.addEffect(new AddCardTypeTargetEffect(Duration.EndOfTurn, CardType.ARTIFACT, CardType.CREATURE), source);
        }
        return true;
    }
}