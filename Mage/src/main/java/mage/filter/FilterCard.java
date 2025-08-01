package mage.filter;

import mage.abilities.Ability;
import mage.cards.Card;
import mage.constants.SubType;
import mage.constants.TargetController;
import mage.filter.predicate.ObjectSourcePlayer;
import mage.filter.predicate.ObjectSourcePlayerPredicate;
import mage.filter.predicate.Predicate;
import mage.filter.predicate.Predicates;
import mage.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Works with cards only. For objects like commanders you must override your canTarget method.
 *
 * @author BetaSteward_at_googlemail.com
 * @author North
 */
public class FilterCard extends FilterObject<Card> {

    private static final long serialVersionUID = 1L;
    protected final List<ObjectSourcePlayerPredicate<Card>> extraPredicates = new ArrayList<>();

    public FilterCard() {
        super("card");
    }

    public FilterCard(String name) {
        this(null, name);
    }

    public FilterCard(SubType subType) {
        this(subType, subType + " card");
    }

    public FilterCard(SubType subType, String name) {
        super(name);
        if (subType != null) {
            this.add(subType.getPredicate());
        }
    }

    protected FilterCard(final FilterCard filter) {
        super(filter);
        this.extraPredicates.addAll(filter.extraPredicates);
    }

    //20130711 708.6c
    /* If anything performs a comparison involving multiple characteristics or
     * values of one or more split cards in any zone other than the stack or
     * involving multiple characteristics or values of one or more fused split
     * spells, each characteristic or value is compared separately. If each of
     * the individual comparisons would return a “yes” answer, the whole
     * comparison returns a “yes” answer. The individual comparisons may involve
     * different halves of the same split card.
     */
    @Override
    public boolean match(Card card, Game game) {
        if (card == null) {
            return false;
        }
        return super.match(card, game);
    }

    public boolean match(Card card, UUID playerId, Ability source, Game game) {
        if (!this.match(card, game)) {
            return false;
        }
        ObjectSourcePlayer<Card> osp = new ObjectSourcePlayer<>(card, playerId, source);
        return extraPredicates.stream().allMatch(p -> p.apply(osp, game));
    }

    public final void add(ObjectSourcePlayerPredicate predicate) {
        if (isLockedFilter()) {
            throw new UnsupportedOperationException("You may not modify a locked filter");
        }

        // verify check
        checkPredicateIsSuitableForCardFilter(predicate);
        Predicates.makeSurePredicateCompatibleWithFilter(predicate, Card.class);

        extraPredicates.add(predicate);
    }

    public boolean hasPredicates() {
        return !predicates.isEmpty() || !extraPredicates.isEmpty();
    }

    @Override
    public FilterCard copy() {
        return new FilterCard(this);
    }

    @Override
    public List<Predicate> getExtraPredicates() {
        return new ArrayList<>(extraPredicates);
    }

    public static void checkPredicateIsSuitableForCardFilter(Predicate predicate) {
        // card filter can't contain controller predicate (only permanents on battlefield have controller)
        List<Predicate> list = new ArrayList<>();
        Predicates.collectAllComponents(predicate, list);
        if (list.stream().anyMatch(TargetController.ControllerPredicate.class::isInstance)) {
            throw new IllegalArgumentException("Wrong code usage: card filter doesn't support controller predicate");
        }
    }

    public FilterCard withMessage(String message) {
        this.setMessage(message);
        return this;
    }
}
