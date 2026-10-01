package mario.decorator;

/**
 * Décorateur concret ajoutant le power-up Étoile d'invincibilité.
 * Augmente la puissance de +100 points et la vitesse de +15 points.
 */
public class StarDecorator extends CharacterDecorator {

    /**
     * Applique le décorateur Étoile au personnage spécifié.
     *
     * @param wrappedCharacter Le personnage à décorer.
     */
    public StarDecorator(Character wrappedCharacter) {
        super(wrappedCharacter);
    }

    @Override
    public String getDescription() {
        return wrappedCharacter.getDescription() + " + Etoile";
    }

    @Override
    public int getPower() {
        return wrappedCharacter.getPower() + 100;
    }

    @Override
    public int getSpeed() {
        return wrappedCharacter.getSpeed() + 15;
    }
}
