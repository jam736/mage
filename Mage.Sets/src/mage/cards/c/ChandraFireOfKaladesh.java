
package mage.cards.c;

import mage.MageInt;
import mage.ObjectColor;
import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.common.SpellCastControllerTriggeredAbility;
import mage.abilities.condition.Condition;
import mage.abilities.condition.common.SourceDealtDamageCondition;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.decorator.ConditionalOneShotEffect;
import mage.abilities.effects.common.DamageTargetEffect;
import mage.abilities.effects.common.ExileAndReturnSourceEffect;
import mage.abilities.effects.common.UntapSourceEffect;
import mage.abilities.keyword.TransformAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.*;
import mage.filter.FilterSpell;
import mage.filter.predicate.mageobject.ColorPredicate;
import mage.target.common.TargetPlayerOrPlaneswalker;
import mage.watchers.common.DamageDoneWatcher;

import java.util.UUID;

/**
 * @author LevelX2
 */
public final class ChandraFireOfKaladesh extends CardImpl {

    private static final FilterSpell filter = new FilterSpell("a red spell");

    static {
        filter.add(new ColorPredicate(ObjectColor.RED));
    }

    private static final Condition condition = new SourceDealtDamageCondition(3);

    public ChandraFireOfKaladesh(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.CREATURE}, "{1}{R}{R}");
        this.supertype.add(SuperType.LEGENDARY);
        this.subtype.add(SubType.HUMAN);
        this.subtype.add(SubType.SHAMAN);
        this.power = new MageInt(2);
        this.toughness = new MageInt(2);

        this.secondSideCardClazz = mage.cards.c.ChandraRoaringFlame.class;

        // Whenever you cast a red spell, untap Chandra, Fire of Kaladesh.
        this.addAbility(new SpellCastControllerTriggeredAbility(new UntapSourceEffect(), filter, false));

        // {T}: Chandra, Fire of Kaladesh deals 1 damage to target player. If Chandra has dealt 3 or more damage this turn, exile her, then return her to the battlefield transformed under her owner's control.        
        this.addAbility(new TransformAbility());
        Ability ability = new SimpleActivatedAbility(new DamageTargetEffect(1), new TapSourceCost());
        ability.addEffect(new ConditionalOneShotEffect(
                new ExileAndReturnSourceEffect(PutCards.BATTLEFIELD_TRANSFORMED, Pronoun.SHE), condition
        ));
        ability.addTarget(new TargetPlayerOrPlaneswalker());
        this.addAbility(ability, new DamageDoneWatcher());
    }

    private ChandraFireOfKaladesh(final ChandraFireOfKaladesh card) {
        super(card);
    }

    @Override
    public ChandraFireOfKaladesh copy() {
        return new ChandraFireOfKaladesh(this);
    }
}
