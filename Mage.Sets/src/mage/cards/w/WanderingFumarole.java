package mage.cards.w;

import mage.abilities.common.EntersBattlefieldTappedAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.continuous.BecomesCreatureSourceEffect;
import mage.abilities.effects.common.continuous.SwitchPowerToughnessSourceEffect;
import mage.abilities.mana.BlueManaAbility;
import mage.abilities.mana.RedManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;
import mage.game.permanent.token.custom.CreatureToken;

import java.util.UUID;

/**
 * @author fireshoes
 */
public final class WanderingFumarole extends CardImpl {

    public WanderingFumarole(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.LAND}, "");

        // Wandering Fumarole enters the battlefield tapped.
        this.addAbility(new EntersBattlefieldTappedAbility());

        // {T}: Add {U} or {R}.
        this.addAbility(new BlueManaAbility());
        this.addAbility(new RedManaAbility());

        // {2}{U}{R}: Until end of turn, Wandering Fumarole becomes a 1/4 blue and red Elemental creature with 
        // "0: Switch this creature's power and toughness until end of turn." It's still a land.
        this.addAbility(new SimpleActivatedAbility(new BecomesCreatureSourceEffect(new CreatureToken(
                1, 4, "1/4 blue and red Elemental creature " +
                "with \"{0}: Switch this creature's power and toughness until end of turn.\"", SubType.ELEMENTAL
        ).withColor("UR").withAbility(new SimpleActivatedAbility(
                new SwitchPowerToughnessSourceEffect(Duration.EndOfTurn), new ManaCostsImpl<>("{0}")
        )), CardType.LAND, Duration.EndOfTurn).withDurationRuleAtStart(true), new ManaCostsImpl<>("{2}{U}{R}")));
    }

    private WanderingFumarole(final WanderingFumarole card) {
        super(card);
    }

    @Override
    public WanderingFumarole copy() {
        return new WanderingFumarole(this);
    }
}
