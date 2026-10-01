package mario.decorator;

/**
 * Décorateur concret ajoutant le power-up Super Champignon.
 * Augmente la santé du personnage de +50 points.
 */
public class SuperMushroomDecorator extends CharacterDecorator {

    /**
     * Applique le décorateur Champignon au personnage spécifié.
     *
     * @param wrappedCharacter Le personnage à décorer.
     */
    public SuperMushroomDecorator(Character wrappedCharacter) {
        super(wrappedCharacter);
    }

    @Override
    public String getDescription() {
        return wrappedCharacter.getDescription() + " + Champignon";
    }

    @Override
    public int getHealth() {
        return wrappedCharacter.getHealth() + 50;
    }
}
