package mage.cards.k;

import mage.abilities.Ability;
import mage.abilities.LoyaltyAbility;
import mage.abilities.common.EntersBattlefieldAllTriggeredAbility;
import mage.abilities.effects.common.DrawCardSourceControllerEffect;
import mage.abilities.effects.common.UntapTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.ComparisonType;
import mage.constants.SubType;
import mage.constants.SuperType;
import mage.filter.FilterPermanent;
import mage.filter.common.FilterControlledCreaturePermanent;
import mage.filter.predicate.mageobject.PowerPredicate;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author TheElk801
 */
public final class KioraBehemothBeckoner extends CardImpl {

    private static final FilterPermanent filter
            = new FilterControlledCreaturePermanent("a creature you control with power 4 or greater");

    static {
        filter.add(new PowerPredicate(ComparisonType.MORE_THAN, 3));
    }

    public KioraBehemothBeckoner(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.PLANESWALKER}, "{2}{G/U}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.KIORA);
        this.setStartingLoyalty(7);

        // Whenever a creature with power 4 or greater you control enters, draw a card.
        this.addAbility(new EntersBattlefieldAllTriggeredAbility(new DrawCardSourceControllerEffect(1), filter));

        // -1: Untap target permanent.
        Ability ability = new LoyaltyAbility(new UntapTargetEffect(), -1);
        ability.addTarget(new TargetPermanent());
        this.addAbility(ability);
    }

    private KioraBehemothBeckoner(final KioraBehemothBeckoner card) {
        super(card);
    }

    @Override
    public KioraBehemothBeckoner copy() {
        return new KioraBehemothBeckoner(this);
    }
}
