package mage.cards.l;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.common.TransformIntoSourceTriggeredAbility;
import mage.abilities.effects.common.ExileUntilSourceLeavesEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.filter.StaticFilters;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author fireshoes
 */
public final class LunarchInquisitors extends CardImpl {

    public LunarchInquisitors(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "");
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.CLERIC);
        this.power = new MageInt(4);
        this.toughness = new MageInt(4);
        this.color.setWhite(true);

        // this card is the second face of double-faced card
        this.nightCard = true;

        // When this creature transforms into Lunarch Inquisitors, you may exile another target creature until Lunarch Inquisitors leaves the battlefield.
        Ability ability = new TransformIntoSourceTriggeredAbility(new ExileUntilSourceLeavesEffect(), true);
        ability.addTarget(new TargetPermanent(StaticFilters.FILTER_ANOTHER_TARGET_CREATURE));
        this.addAbility(ability);
    }

    private LunarchInquisitors(final LunarchInquisitors card) {
        super(card);
    }

    @Override
    public LunarchInquisitors copy() {
        return new LunarchInquisitors(this);
    }
}
