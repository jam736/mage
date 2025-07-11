package mage.cards.y;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.EntersBattlefieldTriggeredAbility;
import mage.abilities.condition.common.KickedCondition;
import mage.abilities.effects.common.continuous.BoostSourceEffect;
import mage.abilities.effects.common.continuous.GainAbilitySourceEffect;
import mage.abilities.keyword.HasteAbility;
import mage.abilities.keyword.KickerAbility;
import mage.abilities.keyword.TrampleAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;

import java.util.UUID;

/**
 * @author freaisdead
 */
public final class YavimayaIconoclast extends CardImpl {

    public YavimayaIconoclast(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{G}");

        this.subtype.add(SubType.ELF);
        this.power = new MageInt(3);
        this.toughness = new MageInt(2);

        // Kicker {R}
        this.addAbility(new KickerAbility("{R}"));

        // Trample
        this.addAbility(TrampleAbility.getInstance());

        // When Yavimaya Iconoclast enters the battlefield, if it was kicked, it gets +1/+1 and gains haste until end of turn.
        Ability ability = new EntersBattlefieldTriggeredAbility(new BoostSourceEffect(
                1, 1, Duration.EndOfTurn
        ).setText("it gets +1/+1")).withInterveningIf(KickedCondition.ONCE);
        ability.addEffect(new GainAbilitySourceEffect(
                HasteAbility.getInstance(), Duration.EndOfTurn
        ).setText("and gains haste until end of turn"));
        this.addAbility(ability);
    }

    private YavimayaIconoclast(final YavimayaIconoclast card) {
        super(card);
    }

    @Override
    public YavimayaIconoclast copy() {
        return new YavimayaIconoclast(this);
    }
}
