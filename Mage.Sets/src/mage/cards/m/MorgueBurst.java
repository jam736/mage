package mage.cards.m;

import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.Zone;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.common.TargetAnyTarget;
import mage.target.common.TargetCardInYourGraveyard;

import java.util.UUID;

/**
 * @author jeffwadsworth
 */
public final class MorgueBurst extends CardImpl {

    public MorgueBurst(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{4}{B}{R}");

        // Return target creature card from your graveyard to your hand. Morgue Burst deals damage to any target equal to the power of the card returned this way.
        this.getSpellAbility().addEffect(new MorgueBurstEffect());
        this.getSpellAbility().addTarget(new TargetCardInYourGraveyard(StaticFilters.FILTER_CARD_CREATURE_YOUR_GRAVEYARD));
        this.getSpellAbility().addTarget(new TargetAnyTarget());
    }

    private MorgueBurst(final MorgueBurst card) {
        super(card);
    }

    @Override
    public MorgueBurst copy() {
        return new MorgueBurst(this);
    }
}

class MorgueBurstEffect extends OneShotEffect {

    MorgueBurstEffect() {
        super(Outcome.ReturnToHand);
        this.staticText = "Return target creature card from your graveyard to your hand. {this} deals damage to any target equal to the power of the card returned this way";
    }

    private MorgueBurstEffect(final MorgueBurstEffect effect) {
        super(effect);
    }

    @Override
    public MorgueBurstEffect copy() {
        return new MorgueBurstEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = game.getCard(source.getFirstTarget());
        if (card != null) {
            Player player = game.getPlayer(card.getOwnerId());
            if (player != null) {
                player.moveCards(card, Zone.HAND, source, game);
                int damage = card.getPower().getValue();
                Permanent creature = game.getPermanent(source.getTargets().get(1).getFirstTarget());
                if (creature != null) {
                    creature.damage(damage, source.getSourceId(), source, game, false, true);
                    return true;
                }
                Player targetPlayer = game.getPlayer(source.getTargets().get(1).getFirstTarget());
                if (targetPlayer != null) {
                    targetPlayer.damage(damage, source.getSourceId(), source, game);
                    return true;
                }
            }
        }
        return false;
    }
}
