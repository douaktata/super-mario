package mario.decorator;

/**
 * Décorateur concret ajoutant le power-up Plume Cape.
 * Augmente la puissance de +5 points et la vitesse de +20 points.
 */
public class CapeFeatherDecorator extends CharacterDecorator {

    /**
     * Applique le décorateur Cape au personnage spécifié.
     *
     * @param wrappedCharacter Le personnage à décorer.
     */
    public CapeFeatherDecorator(Character wrappedCharacter) {
        super(wrappedCharacter);
    }

    @Override
    public String getDescription() {
        return wrappedCharacter.getDescription() + " + Cape";
    }

    @Override
    public int getPower() {
        return wrappedCharacter.getPower() + 5;
    }

    @Override
    public int getSpeed() {
        return wrappedCharacter.getSpeed() + 20;
    }
}
