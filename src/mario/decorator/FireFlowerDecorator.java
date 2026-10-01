package mario.decorator;

/**
 * Décorateur concret ajoutant le power-up Fleur de Feu.
 * Augmente la puissance du personnage de +30 points.
 */
public class FireFlowerDecorator extends CharacterDecorator {

    /**
     * Applique le décorateur Fleur de Feu au personnage spécifié.
     *
     * @param wrappedCharacter Le personnage à décorer.
     */
    public FireFlowerDecorator(Character wrappedCharacter) {
        super(wrappedCharacter);
    }

    @Override
    public String getDescription() {
        return wrappedCharacter.getDescription() + " + Fleur de Feu";
    }

    @Override
    public int getPower() {
        return wrappedCharacter.getPower() + 30;
    }
}
