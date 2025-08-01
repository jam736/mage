
package mage.cards.n;

import mage.abilities.condition.common.SpellMasteryCondition;
import mage.abilities.decorator.ConditionalOneShotEffect;
import mage.abilities.effects.common.search.SearchLibraryPutOntoBattlefieldTappedRestInHandEffect;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.SubType;
import mage.filter.FilterCard;
import mage.filter.common.FilterBasicCard;
import mage.target.common.TargetCardInLibrary;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class NissasPilgrimage extends CardImpl {

    private static final FilterCard filter = new FilterBasicCard(SubType.FOREST, "basic Forest cards");

    public NissasPilgrimage(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.SORCERY}, "{2}{G}");

        // Search your library for up to two basic Forest cards, reveal those cards, and put one onto the battlefield tapped and the rest into your hand. Then shuffle.
        // <i>Spell Mastery</i> &mdash; If there are two or more instant and/or sorcery cards in your graveyard, search your library for up to three basic Forest cards instead of two.
        this.getSpellAbility().addEffect(new ConditionalOneShotEffect(
                new SearchLibraryPutOntoBattlefieldTappedRestInHandEffect(new TargetCardInLibrary(0, 3, filter)),
                new SearchLibraryPutOntoBattlefieldTappedRestInHandEffect(new TargetCardInLibrary(0, 2, filter)),
                SpellMasteryCondition.instance,
                "Search your library for up to two basic Forest cards, reveal those cards, and put one onto the battlefield tapped and the rest into your hand. Then shuffle."
                        + "<br><i>Spell mastery</i> &mdash; If there are two or more instant and/or sorcery cards in your graveyard, search your library for up to three basic Forest cards instead of two."));
    }

    private NissasPilgrimage(final NissasPilgrimage card) {
        super(card);
    }

    @Override
    public NissasPilgrimage copy() {
        return new NissasPilgrimage(this);
    }
}
