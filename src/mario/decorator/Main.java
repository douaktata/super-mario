package mario.decorator;

/**
 * Classe principale de démonstration du Pattern Décorateur.
 * Illustre le cumul des power-ups et prouve la non-régression de l'objet de base.
 */
public class Main {

    /**
     * Constructeur par défaut.
     */
    public Main() {
        // Constructeur explicite pour la javadoc
    }

    /**
     * Formate les informations du personnage selon les exigences du sujet.
     *
     * @param c Le personnage à afficher.
     * @return La chaîne formatée.
     */
    private static String formatCharacter(Character c) {
        return String.format("%s | Sante=%d | Puissance=%d | Vitesse=%d",
                c.getDescription(), c.getHealth(), c.getPower(), c.getSpeed());
    }

    /**
     * Point d'entrée principal.
     *
     * @param args Arguments de ligne de commande (non utilisés).
     */
    public static void main(String[] args) {
        System.out.println("=== DEBUT TEST DECORATEUR SUPER MARIO ===");

        // 1. Mario original
        Character baseMario = new Mario();
        System.out.println(formatCharacter(baseMario));

        // 2. Mario + Champignon
        Character marioMushroom = new SuperMushroomDecorator(baseMario);
        System.out.println(formatCharacter(marioMushroom));

        // 3. Mario + Champignon + Fleur de Feu + Cape
        Character marioCombo = new CapeFeatherDecorator(
                                    new FireFlowerDecorator(
                                        new SuperMushroomDecorator(baseMario)
                                    )
                                );
        System.out.println(formatCharacter(marioCombo));

        // 4. Champignon + Fleur + Cape + Etoile
        Character marioStarCombo = new StarDecorator(marioCombo);
        System.out.println(formatCharacter(marioStarCombo));

        // 5. Ré-affichage du Mario d'origine (Preuve de non-régression)
        System.out.println("\n--- Preuve de non-regression ---");
        System.out.println("Base " + formatCharacter(baseMario));

        System.out.println("=== FIN TEST DECORATEUR ===");
    }
}
