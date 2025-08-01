package mage.cards.m;

import mage.abilities.Ability;
import mage.abilities.common.SimpleActivatedAbility;
import mage.abilities.costs.common.TapSourceCost;
import mage.abilities.costs.mana.ManaCostsImpl;
import mage.abilities.effects.OneShotEffect;
import mage.abilities.effects.common.DamagePlayersEffect;
import mage.abilities.mana.AnyColorManaAbility;
import mage.cards.CardImpl;
import mage.cards.CardSetInfo;
import mage.constants.CardType;
import mage.constants.Outcome;
import mage.constants.SuperType;
import mage.constants.TargetController;
import mage.game.Game;
import mage.game.permanent.Permanent;
import mage.util.RandomUtil;
import mage.watchers.common.AttackedThisTurnWatcher;

import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author LevelX2
 */
public final class MetzaliTowerOfTriumph extends CardImpl {

    public MetzaliTowerOfTriumph(UUID ownerId, CardSetInfo setInfo) {
        super(ownerId, setInfo, new CardType[]{CardType.LAND}, "");

        this.supertype.add(SuperType.LEGENDARY);
        this.nightCard = true;

        // <i>(Transforms from Path of Mettle.)</i>

        // {t}: Add one mana of any color.
        this.addAbility(new AnyColorManaAbility());

        // {1}{R}, {T}: Metzali, Tower of Triumph deals 2 damage to each opponent.
        Ability ability = new SimpleActivatedAbility(new DamagePlayersEffect(2, TargetController.OPPONENT), new ManaCostsImpl<>("{1}{R}"));
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);

        // {2}{W}, {T}: Choose a creature at random that attacked this turn. Destroy that creature.
        ability = new SimpleActivatedAbility(new MetzaliTowerOfTriumphEffect(), new ManaCostsImpl<>("{2}{W}"));
        ability.addCost(new TapSourceCost());
        this.addAbility(ability);
    }

    private MetzaliTowerOfTriumph(final MetzaliTowerOfTriumph card) {
        super(card);
    }

    @Override
    public MetzaliTowerOfTriumph copy() {
        return new MetzaliTowerOfTriumph(this);
    }

}

class MetzaliTowerOfTriumphEffect extends OneShotEffect {

    MetzaliTowerOfTriumphEffect() {
        super(Outcome.DestroyPermanent);
        this.staticText = "choose a creature at random that attacked this turn. Destroy that creature";
    }

    private MetzaliTowerOfTriumphEffect(final MetzaliTowerOfTriumphEffect effect) {
        super(effect);
    }

    @Override
    public MetzaliTowerOfTriumphEffect copy() {
        return new MetzaliTowerOfTriumphEffect(this);
    }

    @Override
    public boolean apply(Game game, Ability source) {
        Permanent permanent = RandomUtil.randomFromCollection(
                game.getState()
                        .getWatcher(AttackedThisTurnWatcher.class)
                        .getAttackedThisTurnCreatures()
                        .stream()
                        .map(mor -> mor.getPermanent(game))
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet())
        );
        return permanent != null && permanent.destroy(source, game);
    }
}
