package mage.cards.i;

import mage.abilities.effects.common.DrawCardSourceControllerEffect;
import mage.abilities.effects.common.ExileThenReturnTargetEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.target.common.TargetControlledCreaturePermanent;

import java.util.UUID;

/**
 * @author Styxo
 */
public final class IllusionistsStratagem extends CardImpl {

    public IllusionistsStratagem(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{3}{U}");

        // Exile up to two target creatures you control, then return those cards to the battlefield under their owner's control.
        this.getSpellAbility().addEffect(new ExileThenReturnTargetEffect(false, true));
        this.getSpellAbility().addTarget(new TargetControlledCreaturePermanent(0, 2));

        // Draw a card.
        this.getSpellAbility().addEffect(new DrawCardSourceControllerEffect(1).concatBy("<br>"));
    }

    private IllusionistsStratagem(final IllusionistsStratagem card) {
        super(card);
    }

    @Override
    public IllusionistsStratagem copy() {
        return new IllusionistsStratagem(this);
    }
}
