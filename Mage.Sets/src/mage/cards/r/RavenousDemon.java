package mage.cards.r;

import mage.MageInt;
import mage.abilities.common.ActivateAsSorceryActivatedAbility;
import mage.abilities.costs.common.SacrificeTargetCost;
import mage.abilities.effects.common.TransformSourceEffect;
import mage.abilities.keyword.TransformAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.filter.common.FilterControlledPermanent;

import java.util.UUID;

/**
 * @author intimidatingant
 */
public final class RavenousDemon extends CardImpl {

    private static final FilterControlledPermanent filter = new FilterControlledPermanent(SubType.HUMAN, "Human");

    public RavenousDemon(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{3}{B}{B}");
        this.subtype.add(SubType.DEMON);

        this.secondSideCardClazz = mage.cards.a.ArchdemonOfGreed.class;

        this.power = new MageInt(4);
        this.toughness = new MageInt(4);

        // Sacrifice a Human: Transform Ravenous Demon. Activate this ability only any time you could cast a sorcery.
        this.addAbility(new TransformAbility());
        this.addAbility(new ActivateAsSorceryActivatedAbility(new TransformSourceEffect(), new SacrificeTargetCost(filter)));
    }

    private RavenousDemon(final RavenousDemon card) {
        super(card);
    }

    @Override
    public RavenousDemon copy() {
        return new RavenousDemon(this);
    }
}
