
package mage.cards.j;

import mage.abilities.Ability;
import mage.abilities.common.DiesAttachedTriggeredAbility;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.AttachEffect;
import mage.abilities.effects.common.ReturnToBattlefieldUnderYourControlAttachedEffect;
import mage.abilities.keyword.EnchantAbility;
import mage.abilities.keyword.TransformAbility;
import mage.cards.Card;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.StaticFilters;
import mage.game.Game;
import mage.players.Player;
import mage.target.TargetPermanent;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class JourneyToEternity extends CardImpl {

    public JourneyToEternity(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.ENCHANTMENT}, "{1}{B}{G}");

        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.AURA);

        this.secondSideCardClazz = mage.cards.a.AtzalCaveOfEternity.class;

        // Enchant creature you control
        TargetPermanent auraTarget = new TargetPermanent(StaticFilters.FILTER_PERMANENT_CREATURE_CONTROLLED);
        this.getSpellAbility().addTarget(auraTarget);
        this.getSpellAbility().addEffect(new AttachEffect(Outcome.BoostCreature));
        this.addAbility(new EnchantAbility(auraTarget));

        // When enchanted creature dies, return it to the battlefield under your control, then return Journey to Eternity to the battlefield transformed under your control.
        this.addAbility(new TransformAbility());
        Ability ability = new DiesAttachedTriggeredAbility(new ReturnToBattlefieldUnderYourControlAttachedEffect("it"), "enchanted creature");
        ability.addEffect(new JourneyToEternityReturnTransformedSourceEffect());
        this.addAbility(ability);

    }

    private JourneyToEternity(final JourneyToEternity card) {
        super(card);
    }

    @Override
    public JourneyToEternity copy() {
        return new JourneyToEternity(this);
    }
}

class JourneyToEternityReturnTransformedSourceEffect extends OneShotEffect {

    JourneyToEternityReturnTransformedSourceEffect() {
        super(Outcome.Benefit);
        this.staticText = ", then return {this} to the battlefield transformed under your control.";
    }

    private JourneyToEternityReturnTransformedSourceEffect(final JourneyToEternityReturnTransformedSourceEffect effect) {
        super(effect);
    }

    @Override
    public JourneyToEternityReturnTransformedSourceEffect copy() {
        return new JourneyToEternityReturnTransformedSourceEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = game.getCard(source.getSourceId());
        Player controller = game.getPlayer(source.getControllerId());
        if (card != null && controller != null) {
            Zone zone = game.getState().getZone(card.getId());
            // cards needs to be in public non battlefield zone
            if (zone == Zone.BATTLEFIELD || !zone.isPublicZone()) {
                return true;
            }
            game.getState().setValue(TransformAbility.VALUE_KEY_ENTER_TRANSFORMED + source.getSourceId(), Boolean.TRUE);
            controller.moveCards(card, Zone.BATTLEFIELD, source, game, false, false, false, null);
        }
        return true;
    }
}
