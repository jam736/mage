package mage.cards.p;

import mage.MageInt;
import mage.abilities.Ability;
import mage.abilities.triggers.BeginningOfUpkeepTriggeredAbility;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.condition.common.SourceInGraveyardCondition;
import mage.abilities.costs.common.SacrificeSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.DoIfCostPaid;
import mage.abilities.effects.common.ReturnToHandSourceEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.constants.TargetController;
import mage.constants.Zone;
import mage.target.common.TargetAnyTarget;

import java.util.UUID;

/**
 *
 * @author LoneFox
 */
public final class PyreZombie extends CardImpl {

    public PyreZombie(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{B}{R}");
        this.subtype.add(SubType.ZOMBIE);
        this.power = new MageInt(2);
        this.toughness = new MageInt(1);

        // At the beginning of your upkeep, if Pyre Zombie is in your graveyard, you may pay {1}{B}{B}. If you do, return Pyre Zombie to your hand.
        this.addAbility(new BeginningOfUpkeepTriggeredAbility(Zone.GRAVEYARD,
                TargetController.YOU, new DoIfCostPaid(new ReturnToHandSourceEffect().setText("return it to your hand"), new ManaCostsImpl<>("{1}{B}{B}")),
                false).withInterveningIf(SourceInGraveyardCondition.instance));

        // {1}{R}{R}, Sacrifice Pyre Zombie: Pyre Zombie deals 2 damage to any target.
        Ability ability = new SimpleActivatedAbility(new DamageTargetEffect(2, "it"), new ManaCostsImpl<>("{1}{R}{R}"));
        ability.addCost(new SacrificeSourceCost());
        ability.addTarget(new TargetAnyTarget());
        this.addAbility(ability);
    }

    private PyreZombie(final PyreZombie card) {
        super(card);
    }

    @Override
    public PyreZombie copy() {
        return new PyreZombie(this);
    }
}
