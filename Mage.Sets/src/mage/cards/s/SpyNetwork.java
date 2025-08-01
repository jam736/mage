
package mage.cards.s;

import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.LookLibraryControllerEffect;
import mage.abilities.effects.common.LookLibraryTopCardTargetPlayerEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.card.FaceDownPredicate;
import mage.filter.predicate.permanent.ControllerIdPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.TargetPlayer;

import java.util.UUID;

/**
 * @author L_J
 */
public final class SpyNetwork extends CardImpl {

    public SpyNetwork(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.INSTANT}, "{U}");

        // Look at target player's hand, the top card of that player's library, and any face-down creatures they control. Look at the top four cards of your library, then put them back in any order.
        this.getSpellAbility().addEffect(new SpyNetworkLookAtTargetPlayerHandEffect());
        this.getSpellAbility().addEffect(new LookLibraryTopCardTargetPlayerEffect().setText(" the top card of that player's library,"));
        this.getSpellAbility().addEffect(new SpyNetworkFaceDownEffect());
        this.getSpellAbility().addEffect(new LookLibraryControllerEffect(4));
        this.getSpellAbility().addTarget(new TargetPlayer());
    }

    private SpyNetwork(final SpyNetwork card) {
        super(card);
    }

    @Override
    public SpyNetwork copy() {
        return new SpyNetwork(this);
    }

}

class SpyNetworkLookAtTargetPlayerHandEffect extends OneShotEffect {

    SpyNetworkLookAtTargetPlayerHandEffect() {
        super(Outcome.Benefit);
        this.staticText = "Look at target player's hand,";
    }

    private SpyNetworkLookAtTargetPlayerHandEffect(final SpyNetworkLookAtTargetPlayerHandEffect effect) {
        super(effect);
    }

    @Override
    public SpyNetworkLookAtTargetPlayerHandEffect copy() {
        return new SpyNetworkLookAtTargetPlayerHandEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player you = game.getPlayer(source.getControllerId());
        Player targetPlayer = game.getPlayer(source.getFirstTarget());
        MageObject sourceObject = game.getObject(source);
        if (you != null && targetPlayer != null) {
            you.lookAtCards("Hand of " + targetPlayer.getName() + " (" + (sourceObject != null ? sourceObject.getIdName() : null) + ')', targetPlayer.getHand(), game);
            return true;
        }
        return false;
    }

}

class SpyNetworkFaceDownEffect extends OneShotEffect {

    SpyNetworkFaceDownEffect() {
        super(Outcome.Benefit);
        this.staticText = "and any face-down creatures they control";
    }

    private SpyNetworkFaceDownEffect(final SpyNetworkFaceDownEffect effect) {
        super(effect);
    }

    @Override
    public SpyNetworkFaceDownEffect copy() {
        return new SpyNetworkFaceDownEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        Player player = game.getPlayer(this.getTargetPointer().getFirst(game, source));
        MageObject mageObject = game.getObject(source);
        if (controller != null && player != null && mageObject != null) {
            FilterCreaturePermanent filter = new FilterCreaturePermanent("face down creature controlled by " + player.getLogName());
            filter.add(FaceDownPredicate.instance);
            filter.add(new ControllerIdPredicate(player.getId()));
            TargetPermanent target = new TargetPermanent(1, 1, filter, true);
            if (target.canChoose(controller.getId(), source, game)) {
                while (controller.chooseUse(outcome, "Look at a face down creature controlled by " + player.getLogName() + "?", source, game)) {
                    target.clearChosen();
                    while (!target.isChosen(game) && target.canChoose(controller.getId(), source, game) && controller.canRespond()) {
                        controller.chooseTarget(outcome, target, source, game);
                    }
                    Permanent faceDownCreature = game.getPermanent(target.getFirstTarget());
                    if (faceDownCreature != null) {
                        controller.lookAtCards("face down card - " + mageObject.getName(), faceDownCreature, game);
                        game.informPlayers(controller.getLogName() + " looks at a face down creature controlled by " + player.getLogName());
                    }
                }
            }
            return true;
        }
        return false;
    }
}
