package mage.cards.h;

import mage.MageInt;
import mage.abilities.common.AttacksTriggeredAbility;
import mage.abilities.condition.common.FerociousCondition;
import mage.abilities.effects.common.continuous.BoostSourceEffect;
import mage.abilities.hint.common.FerociousHint;
import mage.abilities.keyword.DeathtouchAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.AbilityWord;
import mage.constants.CardType;
import mage.constants.Duration;
import mage.constants.SubType;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class HeirOfTheWilds extends CardImpl {

    public HeirOfTheWilds(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{G}");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.WARRIOR);

        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        // Deathtouch
        this.addAbility(DeathtouchAbility.getInstance());

        // <em>Ferocious</em> - Whenever Heir of the Wilds attacks, if you control a creature with power 4 or greater, Heir of the Wilds gets +1/+1 until end of turn.
        this.addAbility(new AttacksTriggeredAbility(
                new BoostSourceEffect(1, 1, Duration.EndOfTurn), false
        ).withInterveningIf(FerociousCondition.instance).addHint(FerociousHint.instance).setAbilityWord(AbilityWord.FEROCIOUS));
    }

    private HeirOfTheWilds(final HeirOfTheWilds card) {
        super(card);
    }

    @Override
    public HeirOfTheWilds copy() {
        return new HeirOfTheWilds(this);
    }
}
