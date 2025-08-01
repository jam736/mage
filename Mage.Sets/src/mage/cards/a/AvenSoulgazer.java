
package mage.cards.a;

import mage.MageInt;
import mage.MageObject;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.keyword.FlyingAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SubType;
import mage.constants.Zone;
import mage.filter.common.FilterCreaturePermanent;
import mage.filter.predicate.card.FaceDownPredicate;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.players.Player;
import mage.target.TargetPermanent;
import mage.target.common.TargetCreaturePermanent;

import java.util.UUID;

/**
 *
 * @author fireshoes
 */
public final class AvenSoulgazer extends CardImpl {
    
    private static final FilterCreaturePermanent filter = new FilterCreaturePermanent("face down creature");

    static {
        filter.add(FaceDownPredicate.instance);
    }

    public AvenSoulgazer(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId,setInfo,new CardType[]{CardType.CREATURE},"{3}{W}{W}");
        this.subtype.add(SubType.BIRD);
        this.subtype.add(SubType.CLERIC);
        this.power = new MageInt(3);
        this.toughness = new MageInt(3);

        // Flying
        this.addAbility(FlyingAbility.getInstance());
        
        // {2}{W}: Look at target face-down creature.
        Ability ability = new SimpleActivatedAbility(new AvenSoulgazerLookFaceDownEffect(), new ManaCostsImpl<>("{2}{W}"));
        ability.addTarget(new TargetPermanent(filter));
        this.addAbility(ability);
    }

    private AvenSoulgazer(final AvenSoulgazer card) {
        super(card);
    }

    @Override
    public AvenSoulgazer copy() {
        return new AvenSoulgazer(this);
    }
}

class AvenSoulgazerLookFaceDownEffect extends OneShotEffect {

    AvenSoulgazerLookFaceDownEffect() {
        super(Outcome.Benefit);
        this.staticText = "Look at target face-down creature";
    }

    private AvenSoulgazerLookFaceDownEffect(final AvenSoulgazerLookFaceDownEffect effect) {
        super(effect);
    }

    @Override
    public AvenSoulgazerLookFaceDownEffect copy() {
        return new AvenSoulgazerLookFaceDownEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player player = game.getPlayer(source.getControllerId());
        MageObject mageObject = game.getObject(source);
        if (player == null || mageObject == null) {
            return false;
        }
        Permanent faceDownCreature = game.getPermanent(getTargetPointer().getFirst(game, source));
        if (faceDownCreature != null) {
            player.lookAtCards("face down cards " + mageObject.getName(), faceDownCreature, game);
        } else {
            return false;
        }
        return true;
    }
}
