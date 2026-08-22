package com.example.data.model

import java.util.UUID

object OmrPresets {

    val BADGES: List<AchievementBadge> = listOf(
        AchievementBadge(
            id = "FIRST_TEST",
            title = "First OMR Test",
            description = "Complete your first digital OMR chapter test.",
            iconEmoji = "🏆",
            pointsReward = 100,
            xpReward = 50,
            isUnlocked = true,
            progress = 1.0f,
            requirementText = "1 test completed"
        ),
        AchievementBadge(
            id = "STREAK_7",
            title = "7-Day Streak",
            description = "Practice OMR challenges 7 days in a row without breaking streak.",
            iconEmoji = "🔥",
            pointsReward = 350,
            xpReward = 150,
            isUnlocked = false,
            progress = 3f / 7f,
            requirementText = "3 / 7 days"
        ),
        AchievementBadge(
            id = "ACCURACY_90",
            title = "90% Precision Master",
            description = "Score 90% or higher accuracy on an OMR test paper.",
            iconEmoji = "🎯",
            pointsReward = 200,
            xpReward = 100,
            isUnlocked = true,
            progress = 1.0f,
            requirementText = "Achieved 92% accuracy"
        ),
        AchievementBadge(
            id = "CHAPTER_MASTER",
            title = "Chapter Master",
            description = "Complete 5 different chapter tests with high accuracy.",
            iconEmoji = "📚",
            pointsReward = 500,
            xpReward = 250,
            isUnlocked = false,
            progress = 3f / 5f,
            requirementText = "3 / 5 chapters"
        ),
        AchievementBadge(
            id = "SPEED_SOLVER",
            title = "Speed Solver",
            description = "Finish a 10+ question test in under 3 minutes with >80% score.",
            iconEmoji = "⚡",
            pointsReward = 250,
            xpReward = 120,
            isUnlocked = false,
            progress = 0.5f,
            requirementText = "Solve fast with high accuracy"
        ),
        AchievementBadge(
            id = "PERFECT_SCORE",
            title = "100% Perfect Score",
            description = "Get every single question right on an OMR test paper.",
            iconEmoji = "💯",
            pointsReward = 600,
            xpReward = 300,
            isUnlocked = false,
            progress = 0.8f,
            requirementText = "No mistakes on a full test"
        ),
        AchievementBadge(
            id = "OMR_LEGEND",
            title = "OMR Arena Legend",
            description = "Accumulate over 5,000 Study Points and reach Scholar level.",
            iconEmoji = "🌟",
            pointsReward = 1000,
            xpReward = 500,
            isUnlocked = false,
            progress = 650f / 5000f,
            requirementText = "650 / 5,000 points"
        )
    )

    val PREMIUM_PERKS: List<PremiumPerkItem> = listOf(
        PremiumPerkItem(
            id = "PERK_DETAILED_ANSWERS",
            title = "Comprehensive Deep-Dive Explanations",
            description = "Unlocks detailed step-by-step conceptual breakdowns, alternative solving methods, and key exam pitfalls.",
            iconEmoji = "💡",
            requiredPoints = 800,
            isUnlocked = false,
            tag = "Most Popular"
        ),
        PremiumPerkItem(
            id = "PERK_FAST_AI",
            title = "Turbo AI Generation",
            description = "Priority GPU compute stream for instant 2-second chapter generation and instantaneous test paper compilation.",
            iconEmoji = "⚡",
            requiredPoints = 1200,
            isUnlocked = false,
            tag = "Speed"
        ),
        PremiumPerkItem(
            id = "PERK_ADVANCED_MISTAKES",
            title = "Advanced AI Mistake Analyzer",
            description = "Generates targeted mini-curriculums that dissect root-cause misconceptions behind every wrong bubble marked.",
            iconEmoji = "🎯",
            requiredPoints = 1500,
            isUnlocked = false,
            tag = "AI Special"
        ),
        PremiumPerkItem(
            id = "PERK_PREMIUM_VOICES",
            title = "Studio AI Multi-Persona Voices",
            description = "Unlocks all 6 hyper-realistic teacher voices (Dr. Sophia, Prof. Marcus, Maya Mentor) across all 11 global languages.",
            iconEmoji = "🎙️",
            requiredPoints = 1800,
            isUnlocked = false,
            tag = "Audio"
        ),
        PremiumPerkItem(
            id = "PERK_UNLIMITED_PROMPTS",
            title = "Unlimited Question Paper Length (50 Qs)",
            description = "Generate massive 50-question mock exams mimicking national board and competitive exams.",
            iconEmoji = "📝",
            requiredPoints = 2500,
            isUnlocked = false,
            tag = "Exams"
        ),
        PremiumPerkItem(
            id = "PERK_FULL_SUITE",
            title = "Full AI Premium Suite (All Unlocked)",
            description = "Permanent lifetime access to all current and future AI features, unlimited tests, and VIP student badge.",
            iconEmoji = "👑",
            requiredPoints = 5000,
            isUnlocked = false,
            tag = "VIP Master"
        )
    )

    val LEADERBOARD_USERS: List<LeaderboardUser> = listOf(
        LeaderboardUser(
            rank = 1,
            username = "Aarav_Scholar",
            avatarEmoji = "🦁",
            studyPoints = 7420,
            xp = 3850,
            streakDays = 18,
            accuracy = 96.2f,
            levelTitle = "Grandmaster"
        ),
        LeaderboardUser(
            rank = 2,
            username = "Elena_BiologyAce",
            avatarEmoji = "🦊",
            studyPoints = 6890,
            xp = 3420,
            streakDays = 14,
            accuracy = 94.8f,
            levelTitle = "Master"
        ),
        LeaderboardUser(
            rank = 3,
            username = "Dev_PhysicsWiz",
            avatarEmoji = "⚡",
            studyPoints = 5940,
            xp = 3100,
            streakDays = 12,
            accuracy = 93.0f,
            levelTitle = "Scholar"
        ),
        LeaderboardUser(
            rank = 4,
            username = "Maya_QuizPro",
            avatarEmoji = "🚀",
            studyPoints = 4850,
            xp = 2650,
            streakDays = 9,
            accuracy = 91.5f,
            levelTitle = "Scholar"
        ),
        LeaderboardUser(
            rank = 5,
            username = "Kenji_MathCoder",
            avatarEmoji = "🐯",
            studyPoints = 4200,
            xp = 2200,
            streakDays = 7,
            accuracy = 89.0f,
            levelTitle = "Smart Student"
        ),
        LeaderboardUser(
            rank = 6,
            username = "You (Current Student)",
            avatarEmoji = "🎓",
            studyPoints = 650,
            xp = 180,
            streakDays = 3,
            accuracy = 84.4f,
            isCurrentUser = true,
            levelTitle = "Learner"
        ),
        LeaderboardUser(
            rank = 7,
            username = "Priya_Curious",
            avatarEmoji = "🌸",
            studyPoints = 590,
            xp = 160,
            streakDays = 2,
            accuracy = 82.0f,
            levelTitle = "Learner"
        ),
        LeaderboardUser(
            rank = 8,
            username = "Lucas_Explorer",
            avatarEmoji = "🦉",
            studyPoints = 480,
            xp = 130,
            streakDays = 2,
            accuracy = 80.5f,
            levelTitle = "Beginner"
        )
    )

    fun getDailyChallengeQuestions(): List<OmrQuestion> = listOf(
        OmrQuestion(
            id = "daily_1",
            questionNumber = 1,
            questionText = "Which organelle is universally referred to as the 'powerhouse of the cell' due to ATP production?",
            options = listOf(
                "A) Golgi apparatus",
                "B) Mitochondria",
                "C) Endoplasmic reticulum",
                "D) Ribosome"
            ),
            correctOption = "B",
            explanation = "Mitochondria generate most of the chemical energy needed to power the cell's biochemical reactions through cellular respiration, storing it as ATP.",
            difficulty = "Easy",
            topic = "Cell Biology"
        ),
        OmrQuestion(
            id = "daily_2",
            questionNumber = 2,
            questionText = "Newton's First Law of Motion is also popularly recognized as the Principle of:",
            options = listOf(
                "A) Acceleration",
                "B) Momentum Conservation",
                "C) Inertia",
                "D) Universal Gravitation"
            ),
            correctOption = "C",
            explanation = "Newton's first law states that an object remains at rest or in uniform straight-line motion unless acted upon by an external net force, known as Inertia.",
            difficulty = "Easy",
            topic = "Physics: Mechanics"
        ),
        OmrQuestion(
            id = "daily_3",
            questionNumber = 3,
            questionText = "What is the pH value of purely neutral distilled water at 25°C?",
            options = listOf(
                "A) 0",
                "B) 5",
                "C) 7",
                "D) 14"
            ),
            correctOption = "C",
            explanation = "At 25°C, pure water has equal concentrations of H+ and OH- ions (10^-7 M each), giving a neutral pH of precisely 7.",
            difficulty = "Easy",
            topic = "Chemistry: Acids & Bases"
        ),
        OmrQuestion(
            id = "daily_4",
            questionNumber = 4,
            questionText = "In the human circulatory system, which blood vessel carries oxygenated blood directly from the lungs back to the left atrium of the heart?",
            options = listOf(
                "A) Pulmonary Artery",
                "B) Pulmonary Vein",
                "C) Superior Vena Cava",
                "D) Aorta"
            ),
            correctOption = "B",
            explanation = "The pulmonary vein is the unique vein in the adult human body that carries freshly oxygenated blood from the lungs into the left atrium.",
            difficulty = "Medium",
            topic = "Human Physiology"
        ),
        OmrQuestion(
            id = "daily_5",
            questionNumber = 5,
            questionText = "If a quadratic equation has discriminant D = b² - 4ac > 0, what is the nature of its roots?",
            options = listOf(
                "A) Two real and distinct roots",
                "B) Two real and equal roots",
                "C) Complex imaginary roots",
                "D) No solution exists"
            ),
            correctOption = "A",
            explanation = "When D > 0, the square root yields a positive real number, generating two real, distinct roots (-b ± √D) / 2a.",
            difficulty = "Medium",
            topic = "Mathematics: Algebra"
        ),
        OmrQuestion(
            id = "daily_6",
            questionNumber = 6,
            questionText = "Which pigment in chloroplasts is primarily responsible for absorbing red and blue light during photosynthesis?",
            options = listOf(
                "A) Carotenoid",
                "B) Chlorophyll a",
                "C) Anthocyanin",
                "D) Xanthophyll"
            ),
            correctOption = "B",
            explanation = "Chlorophyll a is the principal photosynthetic pigment that absorbs sunlight wavelengths in the blue-violet and red ranges while reflecting green.",
            difficulty = "Medium",
            topic = "Plant Biology"
        ),
        OmrQuestion(
            id = "daily_7",
            questionNumber = 7,
            questionText = "What is the SI unit of electric potential difference?",
            options = listOf(
                "A) Ampere",
                "B) Ohm",
                "C) Volt",
                "D) Watt"
            ),
            correctOption = "C",
            explanation = "The Volt (V), defined as 1 Joule per Coulomb of electric charge, is the SI unit of electric potential difference.",
            difficulty = "Easy",
            topic = "Physics: Electricity"
        ),
        OmrQuestion(
            id = "daily_8",
            questionNumber = 8,
            questionText = "Which bond is formed by the complete transfer of one or more valence electrons from one atom to another?",
            options = listOf(
                "A) Covalent Bond",
                "B) Ionic / Electrovalent Bond",
                "C) Hydrogen Bond",
                "D) Metallic Bond"
            ),
            correctOption = "B",
            explanation = "An ionic bond results from the electrostatic attraction between oppositely charged ions formed by the full transfer of electrons.",
            difficulty = "Medium",
            topic = "Chemistry: Chemical Bonding"
        ),
        OmrQuestion(
            id = "daily_9",
            questionNumber = 9,
            questionText = "What is the primary function of stomata in plant leaves?",
            options = listOf(
                "A) Absorption of soil nutrients",
                "B) Gaseous exchange and transpiration",
                "C) Mechanical support",
                "D) Sugar storage"
            ),
            correctOption = "B",
            explanation = "Stomata are microscopic pores regulated by guard cells that permit CO2 intake, O2 release, and transpiration water loss.",
            difficulty = "Easy",
            topic = "Botany"
        ),
        OmrQuestion(
            id = "daily_10",
            questionNumber = 10,
            questionText = "According to Einstein's mass-energy equivalence formula E = mc², what does 'c' represent?",
            options = listOf(
                "A) Speed of light in vacuum (approx 3 x 10^8 m/s)",
                "B) Specific heat capacity",
                "C) Speed of sound in air",
                "D) Universal gravitational constant"
            ),
            correctOption = "A",
            explanation = "'c' represents the constant speed of light in vacuum (~299,792,458 m/s), highlighting that a minute amount of mass converts into colossal energy.",
            difficulty = "Hard",
            topic = "Physics: Modern Physics"
        )
    )

    fun getSampleChapterQuestions(subject: String, chapter: String): List<OmrQuestion> {
        val cleanSub = subject.lowercase()
        return when {
            cleanSub.contains("bio") || cleanSub.contains("cell") || cleanSub.contains("body") || cleanSub.contains("life") -> listOf(
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 1,
                    questionText = "Which cellular structure controls the movement of substances into and out of the cell via selective permeability?",
                    options = listOf(
                        "A) Cell Wall",
                        "B) Plasma / Cell Membrane",
                        "C) Nuclear Envelope",
                        "D) Cytoplasm"
                    ),
                    correctOption = "B",
                    explanation = "The phospholipid bilayer plasma membrane with embedded transport proteins regulates the selective entry and exit of molecules.",
                    difficulty = "Easy",
                    topic = "Membrane Transport"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 2,
                    questionText = "What is the hereditary material composed of nucleotide bases located within the chromosomes of eukaryotic cells?",
                    options = listOf(
                        "A) DNA (Deoxyribonucleic Acid)",
                        "B) ATP",
                        "C) Lipids",
                        "D) Glycogen"
                    ),
                    correctOption = "A",
                    explanation = "DNA stores the genetic code consisting of adenine, thymine, cytosine, and guanine base pairs.",
                    difficulty = "Easy",
                    topic = "Genetics"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 3,
                    questionText = "During which phase of mitosis do sister chromatids pull apart toward opposite poles of the spindle fiber?",
                    options = listOf(
                        "A) Prophase",
                        "B) Metaphase",
                        "C) Anaphase",
                        "D) Telophase"
                    ),
                    correctOption = "C",
                    explanation = "In anaphase, centromeres split and sister chromatids are pulled apart toward opposite cell poles.",
                    difficulty = "Medium",
                    topic = "Cell Division"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 4,
                    questionText = "Which blood cells are responsible for immune defense and phagocytosis of invading pathogens?",
                    options = listOf(
                        "A) Erythrocytes (Red Blood Cells)",
                        "B) Leukocytes (White Blood Cells)",
                        "C) Thrombocytes (Platelets)",
                        "D) Plasma proteins"
                    ),
                    correctOption = "B",
                    explanation = "Leukocytes (WBCs), including lymphocytes, neutrophils, and macrophages, defend against infections.",
                    difficulty = "Easy",
                    topic = "Immunology"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 5,
                    questionText = "Which enzyme in human saliva initiates the chemical digestion of starches into simpler sugars?",
                    options = listOf(
                        "A) Pepsin",
                        "B) Salivary Amylase (Ptyalin)",
                        "C) Trypsin",
                        "D) Lipase"
                    ),
                    correctOption = "B",
                    explanation = "Salivary amylase breaks complex polysaccharides (starches) down into maltose disaccharides in the mouth.",
                    difficulty = "Medium",
                    topic = "Digestive System"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 6,
                    questionText = "Where does the light-dependent stage of photosynthesis occur inside plant cells?",
                    options = listOf(
                        "A) Stroma of chloroplast",
                        "B) Thylakoid membrane",
                        "C) Mitochondrial matrix",
                        "D) Vacuole"
                    ),
                    correctOption = "B",
                    explanation = "The thylakoid membranes contain chlorophyll photosystems I and II where light photons excite electrons to generate ATP and NADPH.",
                    difficulty = "Hard",
                    topic = "Photosynthesis"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 7,
                    questionText = "What type of neuron carries sensory signals from peripheral receptors directly to the central nervous system?",
                    options = listOf(
                        "A) Afferent (Sensory) Neuron",
                        "B) Efferent (Motor) Neuron",
                        "C) Interneuron",
                        "D) Glial cell"
                    ),
                    correctOption = "A",
                    explanation = "Afferent (sensory) neurons transmit incoming sensory stimuli toward the spinal cord and brain.",
                    difficulty = "Medium",
                    topic = "Nervous System"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 8,
                    questionText = "Which hormone produced by beta cells in the islets of Langerhans lowers blood glucose levels?",
                    options = listOf(
                        "A) Glucagon",
                        "B) Insulin",
                        "C) Adrenaline",
                        "D) Thyroxine"
                    ),
                    correctOption = "B",
                    explanation = "Insulin promotes glucose uptake into muscle, liver, and adipose tissues, converting excess glucose to glycogen.",
                    difficulty = "Easy",
                    topic = "Endocrinology"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 9,
                    questionText = "The total number of bones present in an adult human skeletal system is:",
                    options = listOf(
                        "A) 150",
                        "B) 206",
                        "C) 270",
                        "D) 300"
                    ),
                    correctOption = "B",
                    explanation = "An adult human skeleton consists of 206 bones divided into axial (80) and appendicular (126) divisions.",
                    difficulty = "Easy",
                    topic = "Skeletal System"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 10,
                    questionText = "What is the structural and functional filtration unit of the human kidney?",
                    options = listOf(
                        "A) Neuron",
                        "B) Nephron",
                        "C) Alveolus",
                        "D) Hepatocyte"
                    ),
                    correctOption = "B",
                    explanation = "Each kidney contains roughly 1 million nephrons composed of a glomerulus and renal tubule system for ultrafiltration and reabsorption.",
                    difficulty = "Medium",
                    topic = "Excretory System"
                )
            )
            else -> listOf(
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 1,
                    questionText = "What is the rate of change of momentum of a body proportional to according to Newton's Second Law?",
                    options = listOf(
                        "A) Applied Net Force",
                        "B) Body Velocity",
                        "C) Kinetic Energy",
                        "D) Gravitational Field"
                    ),
                    correctOption = "A",
                    explanation = "F = dp/dt = m*a. The applied net force on a body is directly proportional to its rate of change of momentum.",
                    difficulty = "Easy",
                    topic = "Physics: Laws of Motion"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 2,
                    questionText = "Which property of a wave remains unchanged when the wave passes from one medium to another (refraction)?",
                    options = listOf(
                        "A) Velocity",
                        "B) Wavelength",
                        "C) Frequency",
                        "D) Amplitude"
                    ),
                    correctOption = "C",
                    explanation = "The frequency of a wave is determined solely by the source vibration and remains strictly constant during refraction.",
                    difficulty = "Medium",
                    topic = "Physics: Optics & Waves"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 3,
                    questionText = "What is the acceleration due to gravity (g) near the Earth's surface approximately equal to?",
                    options = listOf(
                        "A) 9.8 m/s²",
                        "B) 6.67 x 10^-11 m/s²",
                        "C) 3.0 x 10^8 m/s²",
                        "D) 1.6 x 10^-19 m/s²"
                    ),
                    correctOption = "A",
                    explanation = "The gravitational acceleration near Earth's surface is approximately 9.8 meters per second squared.",
                    difficulty = "Easy",
                    topic = "Physics: Gravitation"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 4,
                    questionText = "Which law states that at constant temperature, the volume of a given mass of gas is inversely proportional to its pressure?",
                    options = listOf(
                        "A) Charles's Law",
                        "B) Boyle's Law",
                        "C) Avogadro's Law",
                        "D) Gay-Lussac's Law"
                    ),
                    correctOption = "B",
                    explanation = "Boyle's Law states P1*V1 = P2*V2 at constant absolute temperature.",
                    difficulty = "Medium",
                    topic = "Chemistry: States of Matter"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 5,
                    questionText = "What is the value of sin²(θ) + cos²(θ) for any angle θ in trigonometry?",
                    options = listOf(
                        "A) 0",
                        "B) 1",
                        "C) 2",
                        "D) tan(θ)"
                    ),
                    correctOption = "B",
                    explanation = "The Pythagorean trigonometric identity states sin²(θ) + cos²(θ) = 1 for all real angles.",
                    difficulty = "Easy",
                    topic = "Mathematics: Trigonometry"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 6,
                    questionText = "Which subatomic particle carries a negative electric charge and revolves around the atomic nucleus?",
                    options = listOf(
                        "A) Proton",
                        "B) Neutron",
                        "C) Electron",
                        "D) Positron"
                    ),
                    correctOption = "C",
                    explanation = "Electrons carry a negative elementary charge (-1.602 x 10^-19 C) and occupy atomic orbitals around the nucleus.",
                    difficulty = "Easy",
                    topic = "Atomic Structure"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 7,
                    questionText = "What is the electric resistance of a conductor with length L and cross-sectional area A proportional to?",
                    options = listOf(
                        "A) Directly to L and inversely to A (R = ρ*L/A)",
                        "B) Inversely to L and directly to A",
                        "C) Directly to both L and A",
                        "D) Independent of dimensions"
                    ),
                    correctOption = "A",
                    explanation = "Resistance R = ρ * (L / A). Longer wires have more resistance, while thicker wires offer lower resistance.",
                    difficulty = "Medium",
                    topic = "Physics: Current Electricity"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 8,
                    questionText = "Which metal is liquid at standard room temperature and pressure (25°C)?",
                    options = listOf(
                        "A) Gallium",
                        "B) Mercury (Hg)",
                        "C) Bromine",
                        "D) Sodium"
                    ),
                    correctOption = "B",
                    explanation = "Mercury (Hg) is the only metallic element that remains liquid at room temperature (melting point -38.8°C).",
                    difficulty = "Easy",
                    topic = "Chemistry: Periodic Table"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 9,
                    questionText = "What is the derivative of f(x) = x³ with respect to x?",
                    options = listOf(
                        "A) 3x",
                        "B) 3x²",
                        "C) x⁴ / 4",
                        "D) 6x"
                    ),
                    correctOption = "B",
                    explanation = "Using the power rule d/dx(x^n) = n*x^(n-1), d/dx(x³) = 3x².",
                    difficulty = "Medium",
                    topic = "Mathematics: Calculus"
                ),
                OmrQuestion(
                    id = UUID.randomUUID().toString(),
                    questionNumber = 10,
                    questionText = "According to the Law of Conservation of Energy, energy can:",
                    options = listOf(
                        "A) Be created from nothing",
                        "B) Be completely destroyed",
                        "C) Neither be created nor destroyed, only transformed from one form to another",
                        "D) Be increased by increasing velocity"
                    ),
                    correctOption = "C",
                    explanation = "The first law of thermodynamics states that the total energy of an isolated system remains constant over time.",
                    difficulty = "Easy",
                    topic = "Physics: Energy"
                )
            )
        }
    }
}
