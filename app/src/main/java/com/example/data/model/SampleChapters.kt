package com.example.data.model

data class SampleChapterPreset(
    val title: String,
    val subject: String,
    val icon: String,
    val excerpt: String
)

object SampleChaptersData {
    val PRESETS = listOf(
        SampleChapterPreset(
            title = "Cellular Respiration & ATP Synthesis",
            subject = "Biology",
            icon = "🧬",
            excerpt = """
                Chapter 9: Cellular Respiration and Fermentation
                Living cells require transfusions of energy from outside sources to perform their tasks. The catabolic pathways of aerobic and anaerobic respiration break down organic molecules and use an electron transport chain for the production of ATP.
                
                1. Glycolysis: Occurring in the cytosol, glycolysis breaks down glucose (a 6-carbon sugar) into two molecules of pyruvate (3 carbons each). This net yields 2 ATP and 2 NADH per glucose without requiring oxygen.
                
                2. Pyruvate Oxidation and the Citric Acid Cycle (Krebs Cycle): Inside the mitochondrial matrix, pyruvate is oxidized to Acetyl-CoA, producing CO2 and NADH. In the Krebs cycle, Acetyl-CoA combines with oxaloacetate to form citrate. Through progressive oxidation steps, 2 ATP, 6 NADH, and 2 FADH2 are produced per glucose molecule.
                
                3. Oxidative Phosphorylation & Chemiosmosis: The electron transport chain, located in the cristae of the inner mitochondrial membrane, accepts electrons from NADH and FADH2. As electrons flow through Complexes I to IV, protons (H+) are pumped into the intermembrane space, creating an electrochemical proton-motive force. Protons flow back through the rotor turbine of ATP Synthase, driving the phosphorylation of ADP to generate approximately 26-28 ATP. Oxygen acts as the terminal electron acceptor, bonding with protons to form H2O.
                
                Anaerobic alternatives like lactic acid fermentation and alcohol fermentation regenerate NAD+ when oxygen is depleted to sustain basal ATP production.
            """.trimIndent()
        ),
        SampleChapterPreset(
            title = "Newton's Laws & Classical Mechanics",
            subject = "Physics",
            icon = "⚡",
            excerpt = """
                Chapter 4: Dynamics and Newton's Laws of Motion
                Dynamics is the study of the causes of motion. Sir Isaac Newton formulated three fundamental laws that govern the relationship between forces and mechanical motion.
                
                First Law (Law of Inertia): An object at rest remains at rest, and an object in continuous linear motion continues at constant velocity unless acted upon by a non-zero net external force (∑F = 0 implies a = 0). Inertia is quantified by inertial mass.
                
                Second Law (Fundamental Equation of Dynamics): The acceleration of an object is directly proportional to the net force acting upon it and inversely proportional to its mass: ∑F = m * a. In vector terms, force equals the time derivative of linear momentum: F = dp/dt. Common forces include gravitational force (Fg = m*g), normal force (Fn), tension (T), and frictional force (Fk = μk * Fn, Fs ≤ μs * Fn).
                
                Third Law (Action-Reaction): Whenever one object exerts a force on a second object, the second object exerts an equal and opposite force on the first: F_AB = -F_BA. These forces always act on distinct bodies and therefore never cancel out within a single free-body diagram.
                
                Applications: Free body diagrams (FBDs) are essential to resolve orthogonal force components in Cartesian coordinates.
            """.trimIndent()
        ),
        SampleChapterPreset(
            title = "The Industrial Revolution & Modern Economy",
            subject = "World History",
            icon = "🏛️",
            excerpt = """
                Chapter 14: The First and Second Industrial Revolutions (1760–1914)
                The transition from agrarian handicraft economies to machine-driven industrial production began in Great Britain during the mid-18th century due to abundant coal deposits, capital accumulation from colonial trade, and agrarian enclosure acts.
                
                Key Innovations:
                - The Steam Engine: Refined by James Watt in 1776, rotary steam engines liberated factories from riverbanks, allowing continuous manufacturing and powering steam locomotives (George Stephenson's Rocket) and steamships.
                - Textile Mechanization: The Spinning Jenny, Water Frame, and Power Loom exponentially increased cotton output and collapsed production costs.
                - Metallurgy: Henry Bessemer's mass steel manufacturing process in 1856 catalyzed urban skyscraper construction and transcontinental railways.
                
                Social and Economic Consequences:
                - Urbanization: Rapid migration to factory towns (Manchester, Birmingham) created dense tenements, poor sanitation, and public health crises.
                - Labor Movements: Long work hours and child labor prompted the rise of trade unions, the Luddite protests, and the Factory Acts of 1833.
                - Ideological Shifts: Adam Smith's Wealth of Nations (laissez-faire capitalism) contrasted with Karl Marx and Friedrich Engels' Communist Manifesto (historical materialism and class struggle).
            """.trimIndent()
        ),
        SampleChapterPreset(
            title = "Graph Algorithms & Shortest Paths",
            subject = "Computer Science",
            icon = "💻",
            excerpt = """
                Chapter 7: Graph Traversal and Shortest Path Optimization
                A graph G = (V, E) consists of a set of vertices V and edges E, which may be directed or undirected, weighted or unweighted.
                
                1. Graph Representations: Adjacency Matrix requires O(V^2) space, ideal for dense graphs. Adjacency List requires O(V + E) space, ideal for sparse graphs.
                
                2. Traversal Algorithms:
                - Breadth-First Search (BFS): Uses a FIFO Queue to explore node layers systematically. Optimal for unweighted shortest path queries with O(V + E) time complexity.
                - Depth-First Search (DFS): Uses recursion or a LIFO Stack to traverse branch depths before backtracking. Used for topological sorting, cycle detection, and strongly connected components (Kosaraju/Tarjan).
                
                3. Single-Source Shortest Paths:
                - Dijkstra's Algorithm: A greedy algorithm using a Min-Priority Queue to find shortest paths from a source in non-negative weighted graphs in O((V + E) log V) time.
                - Bellman-Ford Algorithm: Relaxes all edges |V| - 1 times, capable of handling negative edge weights and detecting negative weight cycles with O(V * E) complexity.
                - A* Search: Augments Dijkstra with an admissible heuristic function h(n) to prioritize target nodes in graph spaces.
            """.trimIndent()
        )
    )
}

data class SnapPresetQuestion(
    val title: String,
    val subject: String,
    val icon: String,
    val questionPrompt: String,
    val previewHint: String
)

object SnapQuestionPresets {
    val PRESETS = listOf(
        SnapPresetQuestion(
            title = "Calculus: Maxima & Derivatives",
            subject = "Mathematics",
            icon = "📐",
            questionPrompt = "Find all critical points of the function f(x) = 2x^3 - 9x^2 + 12x + 5 on the closed interval [-1, 4] and determine the absolute maximum and minimum values.",
            previewHint = "Polynomial differentiation, critical points & Extreme Value Theorem"
        ),
        SnapPresetQuestion(
            title = "Physics: Projectile Trajectory",
            subject = "Physics",
            icon = "🚀",
            questionPrompt = "A projectile is launched from ground level with an initial velocity of v₀ = 15 m/s at an angle of θ = 30° above the horizontal. Assuming g = 9.8 m/s² and negligible air resistance, calculate: 1) Time to reach maximum height, 2) The maximum height reached, and 3) The total horizontal range.",
            previewHint = "2D kinematic decomposition & peak altitude equations"
        ),
        SnapPresetQuestion(
            title = "Chemistry: Limiting Reactant",
            subject = "Chemistry",
            icon = "🧪",
            questionPrompt = "Given the balanced reaction 2A + B → C: 25.0 g of Reactant A (molar mass = 40.0 g/mol) reacts with 18.0 g of Reactant B (molar mass = 32.0 g/mol). Determine: 1) The limiting reactant, 2) Theoretical yield of Product C in moles, and 3) Mass of excess reactant remaining.",
            previewHint = "Stoichiometric mole conversions & theoretical yield"
        ),
        SnapPresetQuestion(
            title = "Biology: Dihybrid Punnett Ratio",
            subject = "Biology",
            icon = "🧬",
            questionPrompt = "In pea plants, Yellow seeds (Y) are dominant to green seeds (y), and Round seeds (R) are dominant to wrinkled seeds (r). Two heterozygous plants (YyRr) are crossed. Explain the step-by-step gamete determination, construct the 16-cell Punnett square, and calculate the phenotypic ratio of offspring.",
            previewHint = "Mendelian independent assortment & 9:3:3:1 ratio derivation"
        ),
        SnapPresetQuestion(
            title = "CompSci: Master Theorem Big-O",
            subject = "Computer Science",
            icon = "⚡",
            questionPrompt = "Analyze the asymptotic time complexity of the divide-and-conquer recurrence relation T(n) = 3T(n/2) + O(n^2) using the Master Theorem. State the values of a, b, and f(n), compare n^(log_b a) with f(n), and prove the final Big-O bound step by step.",
            previewHint = "Divide & conquer recurrence, log ratio comparison & Big-O"
        )
    )
}

