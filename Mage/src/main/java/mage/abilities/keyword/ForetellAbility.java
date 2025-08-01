package mage.abilities.keyword;

import mage.MageObject;
import mage.MageObjectReference;
import mage.abilities.Ability;
import mage.abilities.SpecialAction;
import mage.abilities.SpellAbility;
import mage.abilities.common.SimpleStaticAbility;
import mage.abilities.costs.Cost;
import mage.abilities.costs.Costs;
import mage.abilities.costs.mana.GenericManaCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.AsThoughEffectImpl;
import mage.abilities.effects.ContinuousEffect;
import mage.abilities.effects.ContinuousEffectImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.ExileTargetEffect;
import mage.cards.*;
import mage.constants.*;
import mage.filter.common.FilterNonlandCard;
import mage.filter.predicate.Predicates;
import mage.filter.predicate.mageobject.AbilityPredicate;
import mage.game.ExileZone;
import mage.game.Game;
import mage.game.events.GameEvent;
import mage.players.Player;
import mage.target.targetpointer.FixedTarget;
import mage.util.CardUtil;
import mage.watchers.common.ForetoldWatcher;

import java.util.UUID;

/**
 * @author jeffwadsworth
 */
public class ForetellAbility extends SpecialAction {

    private final String foretellCost;
    private final String foretellSplitCost;
    private final Card card;

    public ForetellAbility(Card card, String foretellCost) {
        this(card, foretellCost, null);
    }

    public ForetellAbility(Card card, String foretellCost, String foretellSplitCost) {
        super(Zone.HAND);
        this.foretellCost = foretellCost;
        this.foretellSplitCost = foretellSplitCost;
        this.card = card;
        this.usesStack = Boolean.FALSE;
        this.addCost(new GenericManaCost(2));
        // exile the card and it can't be cast the turn it was foretold
        this.addEffect(new ForetellExileEffect(card, foretellCost, foretellSplitCost));
        // look at face-down card anytime
        Ability ability = new SimpleStaticAbility(Zone.ALL, new ForetellLookAtCardEffect());
        ability.setControllerId(controllerId);  // if not set, anyone can look at the card in exile
        addSubAbility(ability);
        this.setRuleVisible(true);
        this.addWatcher(new ForetoldWatcher());
    }

    private ForetellAbility(ForetellAbility ability) {
        super(ability);
        this.foretellCost = ability.foretellCost;
        this.foretellSplitCost = ability.foretellSplitCost;
        this.card = ability.card;
    }

    @Override
    public ForetellAbility copy() {
        return new ForetellAbility(this);
    }

    @Override
    public ActivationStatus canActivate(UUID playerId, Game game) {
        // activate only during the controller's turn
        if (game.getState().getContinuousEffects().getApplicableAsThoughEffects(AsThoughEffectType.ALLOW_FORETELL_ANYTIME, game).isEmpty()
                && !game.isActivePlayer(this.getControllerId())) {
            return ActivationStatus.getFalse();
        }
        return super.canActivate(playerId, game);
    }

    @Override
    public String getRule() {
        return "Foretell " + foretellCost + " <i>(During your turn, "
                + "you may pay {2} and exile this card from your hand face down. "
                + "Cast it on a later turn for its foretell cost.)</i>";
    }

    @Override
    public String getGameLogMessage(Game game) {
        return " foretells a card from hand";
    }

    public static boolean isCardInForetell(Card card, Game game) {
        // searching ForetellCostAbility - it adds for foretelled cards only after exile
        return card.getAbilities(game).containsClass(ForetellCostAbility.class);
    }

    public static ContinuousEffect makeAddForetellEffect() {
        return new ForetellAddAbilityEffect();
    }

    /**
     * For use in apply() method of OneShotEffect
     * Exile the target card. It becomes foretold.
     * Its foretell cost is its mana cost reduced by [amountToReduceCost]
     */
    public static boolean doExileBecomesForetold(Card card, Game game, Ability source, int amountToReduceCost) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }

        // process Split, MDFC, and Adventure cards first
        // note that 'Foretell Cost' refers to the main card (left) and 'Foretell Split Cost' refers to the (right) card if it exists
        ForetellAbility foretellAbility = null;
        if (card instanceof SplitCard) {
            String leftHalfCost = CardUtil.reduceCost(((SplitCard) card).getLeftHalfCard().getManaCost(), amountToReduceCost).getText();
            String rightHalfCost = CardUtil.reduceCost(((SplitCard) card).getRightHalfCard().getManaCost(), amountToReduceCost).getText();
            game.getState().setValue(card.getMainCard().getId().toString() + "Foretell Cost", leftHalfCost);
            game.getState().setValue(card.getMainCard().getId().toString() + "Foretell Split Cost", rightHalfCost);
            foretellAbility = new ForetellAbility(card, leftHalfCost, rightHalfCost);
        } else if (card instanceof ModalDoubleFacedCard) {
            ModalDoubleFacedCardHalf leftHalfCard = ((ModalDoubleFacedCard) card).getLeftHalfCard();
            if (!leftHalfCard.isLand(game)) {  // Only MDFC cards with a left side a land have a land on the right side too
                String leftHalfCost = CardUtil.reduceCost(leftHalfCard.getManaCost(), amountToReduceCost).getText();
                game.getState().setValue(card.getMainCard().getId().toString() + "Foretell Cost", leftHalfCost);
                ModalDoubleFacedCardHalf rightHalfCard = ((ModalDoubleFacedCard) card).getRightHalfCard();
                if (rightHalfCard.isLand(game)) {
                    foretellAbility = new ForetellAbility(card, leftHalfCost);
                } else {
                    String rightHalfCost = CardUtil.reduceCost(rightHalfCard.getManaCost(), amountToReduceCost).getText();
                    game.getState().setValue(card.getMainCard().getId().toString() + "Foretell Split Cost", rightHalfCost);
                    foretellAbility = new ForetellAbility(card, leftHalfCost, rightHalfCost);
                }
            }
        } else if (card instanceof CardWithSpellOption) {
            String creatureCost = CardUtil.reduceCost(card.getMainCard().getManaCost(), amountToReduceCost).getText();
            String spellCost = CardUtil.reduceCost(((CardWithSpellOption) card).getSpellCard().getManaCost(), amountToReduceCost).getText();
            game.getState().setValue(card.getMainCard().getId().toString() + "Foretell Cost", creatureCost);
            game.getState().setValue(card.getMainCard().getId().toString() + "Foretell Split Cost", spellCost);
            foretellAbility = new ForetellAbility(card, creatureCost, spellCost);
        } else if (!card.isLand(game)) {
            // normal card
            String costText = CardUtil.reduceCost(card.getManaCost(), amountToReduceCost).getText();
            game.getState().setValue(card.getId().toString() + "Foretell Cost", costText);
            foretellAbility = new ForetellAbility(card, costText);
        }

        // All card types (including lands) must be exiled
        UUID exileId = CardUtil.getExileZoneId(card.getMainCard().getId().toString() + "foretellAbility", game);
        controller.moveCardsToExile(card, source, game, false, exileId, " Foretell Turn Number: " + game.getTurnNum());
        card.setFaceDown(true, game);

        // all done pre-processing so stick the foretell cost effect onto the main card
        // note that the card is not foretell'd into exile, it is put into exile and made foretold
        // If the card is a non-land, it will not be exiled.
        if (foretellAbility != null) {
            // copy source and use it for the foretold effect on the exiled card
            // bug #8673
            Ability copiedSource = source.copy();
            copiedSource.newId();
            copiedSource.setSourceId(card.getId());
            game.getState().setValue(card.getMainCard().getId().toString() + "Foretell Turn Number", game.getTurnNum());
            foretellAbility.setSourceId(card.getId());
            foretellAbility.setControllerId(card.getOwnerId());
            game.getState().addOtherAbility(card, foretellAbility);
            foretellAbility.activate(game, true);
            game.addEffect(new ForetellAddCostEffect(new MageObjectReference(card, game)), copiedSource);
            game.fireEvent(new GameEvent(GameEvent.EventType.CARD_FORETOLD, card.getId(), copiedSource, copiedSource.getControllerId(), 0, false));
        }
        return true;
    }

}

class ForetellExileEffect extends OneShotEffect {

    private final Card card;
    String foretellCost;
    String foretellSplitCost;

    ForetellExileEffect(Card card, String foretellCost, String foretellSplitCost) {
        super(Outcome.Neutral);
        this.card = card;
        this.foretellCost = foretellCost;
        this.foretellSplitCost = foretellSplitCost;
    }

    private ForetellExileEffect(final ForetellExileEffect effect) {
        super(effect);
        this.card = effect.card;
        this.foretellCost = effect.foretellCost;
        this.foretellSplitCost = effect.foretellSplitCost;
    }

    @Override
    public ForetellExileEffect copy() {
        return new ForetellExileEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller != null
                && card != null) {

            // get main card id
            UUID mainCardId = card.getMainCard().getId();

            // retrieve the exileId of the foretold card
            UUID exileId = CardUtil.getExileZoneId(mainCardId.toString() + "foretellAbility", game);

            // foretell turn number shows up on exile window
            ExileTargetEffect effect = new ExileTargetEffect(exileId, " Foretell Turn Number: " + game.getTurnNum());

            // remember turn number it was cast
            game.getState().setValue(mainCardId.toString() + "Foretell Turn Number", game.getTurnNum());

            // remember the foretell cost
            game.getState().setValue(mainCardId.toString() + "Foretell Cost", foretellCost);
            game.getState().setValue(mainCardId.toString() + "Foretell Split Cost", foretellSplitCost);

            // exile the card face-down
            effect.setWithName(false);
            effect.setTargetPointer(new FixedTarget(card.getId(), game));
            effect.apply(game, source);
            card.setFaceDown(true, game);
            game.addEffect(new ForetellAddCostEffect(new MageObjectReference(card, game)), source);
            game.fireEvent(new GameEvent(GameEvent.EventType.CARD_FORETOLD, card.getId(), source, source.getControllerId(), 0, true));
            return true;
        }
        return false;
    }
}

class ForetellLookAtCardEffect extends AsThoughEffectImpl {

    ForetellLookAtCardEffect() {
        super(AsThoughEffectType.LOOK_AT_FACE_DOWN, Duration.EndOfGame, Outcome.AIDontUseIt);
    }

    private ForetellLookAtCardEffect(final ForetellLookAtCardEffect effect) {
        super(effect);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        return true;
    }

    @Override
    public ForetellLookAtCardEffect copy() {
        return new ForetellLookAtCardEffect(this);
    }

    @Override
    public boolean applies(UUID objectId, Ability source, UUID affectedControllerId, Game game) {
        if (affectedControllerId.equals(source.getControllerId())) {
            Card card = game.getCard(objectId);
            if (card != null) {
                MageObject sourceObject = game.getObject(source);
                if (sourceObject == null) {
                    return false;
                }
                UUID mainCardId = card.getMainCard().getId();
                UUID exileId = CardUtil.getExileZoneId(mainCardId.toString() + "foretellAbility", game);
                ExileZone exile = game.getExile().getExileZone(exileId);
                return exile != null
                        && exile.contains(mainCardId);
            }
        }
        return false;
    }
}

class ForetellAddCostEffect extends ContinuousEffectImpl {

    private final MageObjectReference mor;

    ForetellAddCostEffect(MageObjectReference mor) {
        super(Duration.EndOfGame, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        this.mor = mor;
        staticText = "Foretold card";
    }

    private ForetellAddCostEffect(final ForetellAddCostEffect effect) {
        super(effect);
        this.mor = effect.mor;
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Card card = mor.getCard(game);
        if (card != null) {
            UUID mainCardId = card.getMainCard().getId();
            if (game.getState().getZone(mainCardId) == Zone.EXILED) {
                String foretellCost = (String) game.getState().getValue(mainCardId.toString() + "Foretell Cost");
                String foretellSplitCost = (String) game.getState().getValue(mainCardId.toString() + "Foretell Split Cost");
                if (card instanceof SplitCard) {
                    if (foretellCost != null) {
                        SplitCardHalf leftHalfCard = ((SplitCard) card).getLeftHalfCard();
                        ForetellCostAbility ability = new ForetellCostAbility(foretellCost);
                        ability.setSourceId(leftHalfCard.getId());
                        ability.setControllerId(source.getControllerId());
                        ability.setSpellAbilityType(leftHalfCard.getSpellAbility().getSpellAbilityType());
                        ability.setAbilityName(leftHalfCard.getName());
                        game.getState().addOtherAbility(leftHalfCard, ability);
                    }
                    if (foretellSplitCost != null) {
                        SplitCardHalf rightHalfCard = ((SplitCard) card).getRightHalfCard();
                        ForetellCostAbility ability = new ForetellCostAbility(foretellSplitCost);
                        ability.setSourceId(rightHalfCard.getId());
                        ability.setControllerId(source.getControllerId());
                        ability.setSpellAbilityType(rightHalfCard.getSpellAbility().getSpellAbilityType());
                        ability.setAbilityName(rightHalfCard.getName());
                        game.getState().addOtherAbility(rightHalfCard, ability);
                    }
                } else if (card instanceof ModalDoubleFacedCard) {
                    if (foretellCost != null) {
                        ModalDoubleFacedCardHalf leftHalfCard = ((ModalDoubleFacedCard) card).getLeftHalfCard();
                        // some MDFC's are land IE: sea gate restoration
                        if (!leftHalfCard.isLand(game)) {
                            ForetellCostAbility ability = new ForetellCostAbility(foretellCost);
                            ability.setSourceId(leftHalfCard.getId());
                            ability.setControllerId(source.getControllerId());
                            ability.setSpellAbilityType(leftHalfCard.getSpellAbility().getSpellAbilityType());
                            ability.setAbilityName(leftHalfCard.getName());
                            game.getState().addOtherAbility(leftHalfCard, ability);
                        }
                    }
                    if (foretellSplitCost != null) {
                        ModalDoubleFacedCardHalf rightHalfCard = ((ModalDoubleFacedCard) card).getRightHalfCard();
                        // some MDFC's are land IE: sea gate restoration
                        if (!rightHalfCard.isLand(game)) {
                            ForetellCostAbility ability = new ForetellCostAbility(foretellSplitCost);
                            ability.setSourceId(rightHalfCard.getId());
                            ability.setControllerId(source.getControllerId());
                            ability.setSpellAbilityType(rightHalfCard.getSpellAbility().getSpellAbilityType());
                            ability.setAbilityName(rightHalfCard.getName());
                            game.getState().addOtherAbility(rightHalfCard, ability);
                        }
                    }
                } else if (card instanceof CardWithSpellOption) {
                    if (foretellCost != null) {
                        Card creatureCard = card.getMainCard();
                        ForetellCostAbility ability = new ForetellCostAbility(foretellCost);
                        ability.setSourceId(creatureCard.getId());
                        ability.setControllerId(source.getControllerId());
                        ability.setSpellAbilityType(creatureCard.getSpellAbility().getSpellAbilityType());
                        ability.setAbilityName(creatureCard.getName());
                        game.getState().addOtherAbility(creatureCard, ability);
                    }
                    if (foretellSplitCost != null) {
                        Card spellCard = ((CardWithSpellOption) card).getSpellCard();
                        ForetellCostAbility ability = new ForetellCostAbility(foretellSplitCost);
                        ability.setSourceId(spellCard.getId());
                        ability.setControllerId(source.getControllerId());
                        ability.setSpellAbilityType(spellCard.getSpellAbility().getSpellAbilityType());
                        ability.setAbilityName(spellCard.getName());
                        game.getState().addOtherAbility(spellCard, ability);
                    }
                } else if (foretellCost != null) {
                    ForetellCostAbility ability = new ForetellCostAbility(foretellCost);
                    ability.setSourceId(card.getId());
                    ability.setControllerId(source.getControllerId());
                    ability.setSpellAbilityType(card.getSpellAbility().getSpellAbilityType());
                    ability.setAbilityName(card.getName());
                    game.getState().addOtherAbility(card, ability);
                }
                return true;
            }
        }
        discard();
        return true;
    }

    @Override
    public ForetellAddCostEffect copy() {
        return new ForetellAddCostEffect(this);
    }
}

class ForetellCostAbility extends SpellAbility {

    private String abilityName;
    private SpellAbility spellAbilityToResolve;

    ForetellCostAbility(String foretellCost) {
        super(null, "Testing", Zone.EXILED, SpellAbilityType.BASE_ALTERNATE, SpellAbilityCastMode.NORMAL);
        // Needed for Dream Devourer and Ethereal Valkyrie reducing the cost of a colorless CMC 2 or less spell to 0
        // CardUtil.reduceCost returns an empty string in that case so we add a cost of 0 here
        // https://github.com/magefree/mage/issues/7607
        if (foretellCost != null && foretellCost.isEmpty()) {
            foretellCost = "{0}";
        }
        this.setAdditionalCostsRuleVisible(false);
        this.name = "Foretell " + foretellCost;
        this.addCost(new ManaCostsImpl<>(foretellCost));
    }

    private ForetellCostAbility(final ForetellCostAbility ability) {
        super(ability);
        this.spellAbilityType = ability.spellAbilityType;
        this.abilityName = ability.abilityName;
        this.spellAbilityToResolve = ability.spellAbilityToResolve;
    }

    @Override
    public ActivationStatus canActivate(UUID playerId, Game game) {
        if (super.canActivate(playerId, game).canActivate()) {
            Card card = game.getCard(getSourceId());
            if (card != null) {
                UUID mainCardId = card.getMainCard().getId();
                // Card must be in the exile zone
                if (game.getState().getZone(mainCardId) != Zone.EXILED) {
                    return ActivationStatus.getFalse();
                }
                Integer foretoldTurn = (Integer) game.getState().getValue(mainCardId.toString() + "Foretell Turn Number");
                UUID exileId = (UUID) game.getState().getValue(mainCardId.toString() + "foretellAbility");
                // Card must be Foretold
                if (foretoldTurn == null || exileId == null) {
                    return ActivationStatus.getFalse();
                }
                // Can't be cast if the turn it was Foretold is the same
                if (foretoldTurn == game.getTurnNum()) {
                    return ActivationStatus.getFalse();
                }
                // Check that the card is actually in the exile zone (ex: Oblivion Ring exiles it after it was Foretold, etc)
                ExileZone exileZone = game.getState().getExile().getExileZone(exileId);
                if (exileZone != null
                        && exileZone.isEmpty()) {
                    return ActivationStatus.getFalse();
                }
                if (card instanceof SplitCard) {
                    if (((SplitCard) card).getLeftHalfCard().getName().equals(abilityName)) {
                        return ((SplitCard) card).getLeftHalfCard().getSpellAbility().canActivate(playerId, game);
                    } else if (((SplitCard) card).getRightHalfCard().getName().equals(abilityName)) {
                        return ((SplitCard) card).getRightHalfCard().getSpellAbility().canActivate(playerId, game);
                    }
                } else if (card instanceof ModalDoubleFacedCard) {
                    if (((ModalDoubleFacedCard) card).getLeftHalfCard().getName().equals(abilityName)) {
                        return ((ModalDoubleFacedCard) card).getLeftHalfCard().getSpellAbility().canActivate(playerId, game);
                    } else if (((ModalDoubleFacedCard) card).getRightHalfCard().getName().equals(abilityName)) {
                        return ((ModalDoubleFacedCard) card).getRightHalfCard().getSpellAbility().canActivate(playerId, game);
                    }
                } else if (card instanceof CardWithSpellOption) {
                    if (card.getMainCard().getName().equals(abilityName)) {
                        return card.getMainCard().getSpellAbility().canActivate(playerId, game);
                    } else if (((CardWithSpellOption) card).getSpellCard().getName().equals(abilityName)) {
                        return ((CardWithSpellOption) card).getSpellCard().getSpellAbility().canActivate(playerId, game);
                    }
                }
                return card.getSpellAbility().canActivate(playerId, game);
            }
        }
        return ActivationStatus.getFalse();
    }

    @Override
    public SpellAbility getSpellAbilityToResolve(Game game) {
        Card card = game.getCard(getSourceId());
        if (card != null) {
            if (spellAbilityToResolve == null) {
                SpellAbility spellAbilityCopy = null;
                if (card instanceof SplitCard) {
                    if (((SplitCard) card).getLeftHalfCard().getName().equals(abilityName)) {
                        spellAbilityCopy = ((SplitCard) card).getLeftHalfCard().getSpellAbility().copy();
                    } else if (((SplitCard) card).getRightHalfCard().getName().equals(abilityName)) {
                        spellAbilityCopy = ((SplitCard) card).getRightHalfCard().getSpellAbility().copy();
                    }
                } else if (card instanceof ModalDoubleFacedCard) {
                    if (((ModalDoubleFacedCard) card).getLeftHalfCard().getName().equals(abilityName)) {
                        spellAbilityCopy = ((ModalDoubleFacedCard) card).getLeftHalfCard().getSpellAbility().copy();
                    } else if (((ModalDoubleFacedCard) card).getRightHalfCard().getName().equals(abilityName)) {
                        spellAbilityCopy = ((ModalDoubleFacedCard) card).getRightHalfCard().getSpellAbility().copy();
                    }
                } else if (card instanceof CardWithSpellOption) {
                    if (card.getMainCard().getName().equals(abilityName)) {
                        spellAbilityCopy = card.getMainCard().getSpellAbility().copy();
                    } else if (((CardWithSpellOption) card).getSpellCard().getName().equals(abilityName)) {
                        spellAbilityCopy = ((CardWithSpellOption) card).getSpellCard().getSpellAbility().copy();
                    }
                } else {
                    spellAbilityCopy = card.getSpellAbility().copy();
                }
                if (spellAbilityCopy == null) {
                    return null;
                }
                spellAbilityCopy.setId(this.getId());
                spellAbilityCopy.clearManaCosts();
                spellAbilityCopy.clearManaCostsToPay();
                spellAbilityCopy.addCost(this.getCosts().copy());
                spellAbilityCopy.addCost(this.getManaCosts().copy());
                spellAbilityCopy.setSpellAbilityCastMode(this.getSpellAbilityCastMode());
                spellAbilityToResolve = spellAbilityCopy;
            }
        }
        return spellAbilityToResolve;
    }

    @Override
    public Costs<Cost> getCosts() {
        if (spellAbilityToResolve == null) {
            return super.getCosts();
        }
        return spellAbilityToResolve.getCosts();
    }

    @Override
    public ForetellCostAbility copy() {
        return new ForetellCostAbility(this);
    }

    @Override
    public String getRule(boolean all) {
        StringBuilder sbRule = new StringBuilder("Foretell");
        if (!getCosts().isEmpty()) {
            sbRule.append("&mdash;");
        } else {
            sbRule.append(' ');
        }
        if (!getManaCosts().isEmpty()) {
            sbRule.append(getManaCosts().getText());
        }
        if (!getCosts().isEmpty()) {
            if (!getManaCosts().isEmpty()) {
                sbRule.append(", ");
            }
            sbRule.append(getCosts().getText());
            sbRule.append('.');
        }
        if (abilityName != null) {
            sbRule.append(' ');
            sbRule.append(abilityName);
        }
        sbRule.append(" <i>(You may cast this card from exile for its foretell cost.)</i>");
        return sbRule.toString();
    }

    /**
     * Used for split card in PlayerImpl method:
     * getOtherUseableActivatedAbilities
     */
    void setAbilityName(String abilityName) {
        this.abilityName = abilityName;
    }

}

class ForetellAddAbilityEffect extends ContinuousEffectImpl {

    private static final FilterNonlandCard filter = new FilterNonlandCard();

    static {
        filter.add(Predicates.not(new AbilityPredicate(ForetellAbility.class)));
    }

    ForetellAddAbilityEffect() {
        super(Duration.WhileOnBattlefield, Layer.AbilityAddingRemovingEffects_6, SubLayer.NA, Outcome.AddAbility);
        this.staticText = "Each nonland card in your hand without foretell has foretell. Its foretell cost is equal to its mana cost reduced by {2}";
    }

    private ForetellAddAbilityEffect(final ForetellAddAbilityEffect effect) {
        super(effect);
    }

    @Override
    public ForetellAddAbilityEffect copy() {
        return new ForetellAddAbilityEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Player controller = game.getPlayer(source.getControllerId());
        if (controller == null) {
            return false;
        }
        for (Card card : controller.getHand().getCards(filter, game)) {
            ForetellAbility foretellAbility = null;
            if (card instanceof SplitCard) {
                String leftHalfCost = CardUtil.reduceCost(((SplitCard) card).getLeftHalfCard().getManaCost(), 2).getText();
                String rightHalfCost = CardUtil.reduceCost(((SplitCard) card).getRightHalfCard().getManaCost(), 2).getText();
                foretellAbility = new ForetellAbility(card, leftHalfCost, rightHalfCost);
            } else if (card instanceof ModalDoubleFacedCard) {
                ModalDoubleFacedCardHalf leftHalfCard = ((ModalDoubleFacedCard) card).getLeftHalfCard();
                // If front side of MDFC is land, do nothing as Dream Devourer does not apply to lands
                // MDFC cards in hand are considered lands if front side is land
                if (!leftHalfCard.isLand(game)) {
                    String leftHalfCost = CardUtil.reduceCost(leftHalfCard.getManaCost(), 2).getText();
                    ModalDoubleFacedCardHalf rightHalfCard = ((ModalDoubleFacedCard) card).getRightHalfCard();
                    if (rightHalfCard.isLand(game)) {
                        foretellAbility = new ForetellAbility(card, leftHalfCost);
                    } else {
                        String rightHalfCost = CardUtil.reduceCost(rightHalfCard.getManaCost(), 2).getText();
                        foretellAbility = new ForetellAbility(card, leftHalfCost, rightHalfCost);
                    }
                }
            } else if (card instanceof CardWithSpellOption) {
                String creatureCost = CardUtil.reduceCost(card.getMainCard().getManaCost(), 2).getText();
                String spellCost = CardUtil.reduceCost(((CardWithSpellOption) card).getSpellCard().getManaCost(), 2).getText();
                foretellAbility = new ForetellAbility(card, creatureCost, spellCost);
            } else {
                String costText = CardUtil.reduceCost(card.getManaCost(), 2).getText();
                foretellAbility = new ForetellAbility(card, costText);
            }
            if (foretellAbility != null) {
                foretellAbility.setSourceId(card.getId());
                foretellAbility.setControllerId(card.getOwnerId());
                game.getState().addOtherAbility(card, foretellAbility);
            }
        }
        return true;
    }
}
