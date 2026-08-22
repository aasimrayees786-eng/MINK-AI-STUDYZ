package com.example.data.model

enum class HologramVisualType(val label: String, val icon: String) {
    BONES_SKELETON("Skeletal System", "🦴"),
    HEART_CARDIO("Circulatory Heart", "🫀"),
    BRAIN_NEURAL("Neural Brain", "🧠"),
    SOLAR_SYSTEM("Solar System", "🪐"),
    ATOM_MOLECULE("Atomic Orbitals", "⚛️"),
    DNA_HELIX("DNA Double Helix", "🧬"),
    CELL_PHOTOSYNTHESIS("Plant Cell & Chloroplast", "🌿"),
    GRAVITY_PHYSICS("Gravity & Spacetime", "🌌"),
    AI_CONCEPT_DIAGRAM("Smart Concept Map", "📊")
}

enum class RobotGesture {
    POINT_RIGHT,
    POINT_LEFT,
    WELCOME_OPEN,
    EXPLAINING_HANDS,
    THINKING_CHIN,
    EXCITED_BOTH
}

enum class RobotEmotion {
    HAPPY,
    ENTHUSIASTIC,
    CURIOUS,
    SERIOUS_FOCUS
}

data class RobotSpeechCue(
    val id: String,
    val text: String,
    val keyword: String,
    val visualType: HologramVisualType,
    val visualTitle: String,
    val visualSubtitle: String,
    val visualLabels: List<String> = emptyList(),
    val robotGesture: RobotGesture = RobotGesture.POINT_RIGHT,
    val robotEmotion: RobotEmotion = RobotEmotion.ENTHUSIASTIC,
    val highlightFact: String = "",
    val durationSecondsEstimate: Float = 5.0f
)

data class RobotLesson(
    val id: String,
    val title: String,
    val subject: String,
    val description: String,
    val thumbnailIcon: String,
    val difficulty: String = "Interactive",
    val cues: List<RobotSpeechCue>
)

object PresetRobotLessons {
    val LESSON_BONES = RobotLesson(
        id = "lesson_bones_206",
        title = "The Human Skeleton & 206 Bones",
        subject = "Human Anatomy & Biology",
        description = "Discover the incredible architecture of the human skeletal system, axial vs appendicular bones, and bone marrow function.",
        thumbnailIcon = "🦴",
        cues = listOf(
            RobotSpeechCue(
                id = "cue_b1",
                text = "Hello future scientists! Welcome to our virtual anatomy laboratory.",
                keyword = "Welcome",
                visualType = HologramVisualType.BONES_SKELETON,
                visualTitle = "Human Skeletal Architecture",
                visualSubtitle = "Structural Framework of the Human Body",
                visualLabels = listOf("Cranium (Skull)", "Spinal Vertebrae", "Rib Cage", "Femur"),
                robotGesture = RobotGesture.WELCOME_OPEN,
                robotEmotion = RobotEmotion.HAPPY,
                highlightFact = "Provides structural support, organ protection & movement"
            ),
            RobotSpeechCue(
                id = "cue_b2",
                text = "Did you know that an adult human body has exactly 206 bones working together in harmony?",
                keyword = "bones",
                visualType = HologramVisualType.BONES_SKELETON,
                visualTitle = "206 Interconnected Bones",
                visualSubtitle = "80 Axial Bones • 126 Appendicular Bones",
                visualLabels = listOf("206 Total Bones", "Calcium Phosphate Matrix", "Collagen Flexibility", "Joint Articulations"),
                robotGesture = RobotGesture.POINT_RIGHT,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "Infants are born with ~270 bones which fuse during development"
            ),
            RobotSpeechCue(
                id = "cue_b3",
                text = "Inside your largest bone, the femur, bone marrow produces over two million red blood cells every single second!",
                keyword = "femur",
                visualType = HologramVisualType.BONES_SKELETON,
                visualTitle = "Femur & Hematopoiesis",
                visualSubtitle = "Longest & Strongest Bone in the Body",
                visualLabels = listOf("Compact Bone Shell", "Spongy Bone Lattice", "Red Bone Marrow", "2 Million RBCs / sec"),
                robotGesture = RobotGesture.EXPLAINING_HANDS,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "The femur can resist forces of up to 30 times human body weight"
            ),
            RobotSpeechCue(
                id = "cue_b4",
                text = "Your axial skeleton protects your delicate brain, spinal cord, and heart from any external impact.",
                keyword = "skeleton",
                visualType = HologramVisualType.BONES_SKELETON,
                visualTitle = "Axial Protective Cage",
                visualSubtitle = "Skull, Thoracic Cage & Vertebral Column",
                visualLabels = listOf("22 Cranial Bones", "24 Rib Pairs", "Sternum Shield", "33 Vertebrae"),
                robotGesture = RobotGesture.POINT_RIGHT,
                robotEmotion = RobotEmotion.SERIOUS_FOCUS,
                highlightFact = "The human rib cage moves up to 20,000 times a day during respiration"
            )
        )
    )

    val LESSON_HEART = RobotLesson(
        id = "lesson_heart_cardio",
        title = "The Human Heart & Circulatory Engine",
        subject = "Cardiovascular Physiology",
        description = "Step inside the pulsating 4-chamber human heart and trace oxygenated blood flow throughout the body.",
        thumbnailIcon = "🫀",
        cues = listOf(
            RobotSpeechCue(
                id = "cue_h1",
                text = "Greetings! Today we are exploring the most tireless muscular pump in nature: the human heart.",
                keyword = "heart",
                visualType = HologramVisualType.HEART_CARDIO,
                visualTitle = "4-Chambered Muscular Engine",
                visualSubtitle = "Pumps ~7,500 Liters of Blood Daily",
                visualLabels = listOf("Right Atrium", "Left Atrium", "Right Ventricle", "Left Ventricle"),
                robotGesture = RobotGesture.WELCOME_OPEN,
                robotEmotion = RobotEmotion.HAPPY,
                highlightFact = "Beats approximately 100,000 times every day without pausing"
            ),
            RobotSpeechCue(
                id = "cue_h2",
                text = "The heart contains four specialized chambers: the upper atria receive blood, while the lower ventricles forcefully pump it out.",
                keyword = "ventricles",
                visualType = HologramVisualType.HEART_CARDIO,
                visualTitle = "Atrial & Ventricular Flow",
                visualSubtitle = "Synchronized Cardiac Cycle (Systole & Diastole)",
                visualLabels = listOf("Deoxygenated Blue Inflow", "Oxygenated Red Ejection", "Tricuspid Valve", "Bicuspid/Mitral Valve"),
                robotGesture = RobotGesture.POINT_RIGHT,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "The left ventricle wall is 3x thicker to pump blood to the entire body"
            ),
            RobotSpeechCue(
                id = "cue_h3",
                text = "Through the mighty aorta, fresh oxygenated blood travels across 60,000 miles of blood vessels to nourish every cell.",
                keyword = "aorta",
                visualType = HologramVisualType.HEART_CARDIO,
                visualTitle = "Aorta & Systemic Circulation",
                visualSubtitle = "60,000 Miles of Capillary Highways",
                visualLabels = listOf("Aortic Arch", "Pulmonary Artery", "Coronary Arteries", "120/80 mmHg Peak Pressure"),
                robotGesture = RobotGesture.EXPLAINING_HANDS,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "Blood travels from the heart to your toes and back in under 45 seconds"
            )
        )
    )

    val LESSON_BRAIN = RobotLesson(
        id = "lesson_brain_neuro",
        title = "The Human Brain & 86 Billion Neurons",
        subject = "Neuroscience & Cognitive Science",
        description = "Visualize neural synaptic firings, cerebral hemispheres, and how thoughts are processed at 270 mph.",
        thumbnailIcon = "🧠",
        cues = listOf(
            RobotSpeechCue(
                id = "cue_br1",
                text = "Behold the command center of consciousness: the human brain, containing roughly 86 billion neurons!",
                keyword = "brain",
                visualType = HologramVisualType.BRAIN_NEURAL,
                visualTitle = "Cerebral Cortex & Neural Web",
                visualSubtitle = "86 Billion Biological Supercomputers",
                visualLabels = listOf("Frontal Lobe (Logic)", "Parietal Lobe (Sensory)", "Occipital Lobe (Vision)", "Temporal Lobe (Memory)"),
                robotGesture = RobotGesture.POINT_RIGHT,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "Generates roughly 20 watts of electricity, enough to power an LED light bulb"
            ),
            RobotSpeechCue(
                id = "cue_br2",
                text = "Whenever you learn a new concept, electrical impulses leap across microscopic gaps called synapses at over 260 miles per hour!",
                keyword = "synapses",
                visualType = HologramVisualType.BRAIN_NEURAL,
                visualTitle = "Synaptic Transmission",
                visualSubtitle = "Neurotransmitters & Action Potentials",
                visualLabels = listOf("Dendrites", "Axon Terminal", "Dopamine & Serotonin", "100 Trillion Synapses"),
                robotGesture = RobotGesture.EXCITED_BOTH,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "Your brain forms new synaptic connections every time you study"
            ),
            RobotSpeechCue(
                id = "cue_br3",
                text = "The hippocampus consolidates your short-term memories into long-term retention while you sleep.",
                keyword = "memory",
                visualType = HologramVisualType.BRAIN_NEURAL,
                visualTitle = "Hippocampus & Memory Engine",
                visualSubtitle = "Memory Consolidation & Neuroplasticity",
                visualLabels = listOf("Hippocampus", "Amygdala (Emotion)", "Prefrontal Cortex", "Spaced Recall Activation"),
                robotGesture = RobotGesture.THINKING_CHIN,
                robotEmotion = RobotEmotion.CURIOUS,
                highlightFact = "Sleep strengthens synaptic pathways needed for exam recall"
            )
        )
    )

    val LESSON_SOLAR = RobotLesson(
        id = "lesson_solar_system",
        title = "Journey Through Our Solar System",
        subject = "Astrophysics & Space Science",
        description = "Orbit the planets, study planetary scales, gravitational ellipses, and astronomical marvels.",
        thumbnailIcon = "🪐",
        cues = listOf(
            RobotSpeechCue(
                id = "cue_s1",
                text = "Buckle up astronauts! We are embarking on a high-speed cosmic tour of our solar system.",
                keyword = "planets",
                visualType = HologramVisualType.SOLAR_SYSTEM,
                visualTitle = "The Solar Planetary System",
                visualSubtitle = "8 Major Planets • 1 Yellow Dwarf Star",
                visualLabels = listOf("Sun Core", "Mercury", "Venus", "Earth & Moon", "Mars"),
                robotGesture = RobotGesture.WELCOME_OPEN,
                robotEmotion = RobotEmotion.HAPPY,
                highlightFact = "The Sun contains 99.86% of all mass in the entire solar system"
            ),
            RobotSpeechCue(
                id = "cue_s2",
                text = "Past the rocky inner planets and asteroid belt lies Jupiter, the colossal gas giant with over 90 moons.",
                keyword = "Jupiter",
                visualType = HologramVisualType.SOLAR_SYSTEM,
                visualTitle = "Gas Giants & Ring Systems",
                visualSubtitle = "Jupiter, Saturn, Uranus & Neptune",
                visualLabels = listOf("Great Red Spot", "Saturn's Ice Rings", "Europa Ocean", "Titan Atmosphere"),
                robotGesture = RobotGesture.POINT_RIGHT,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "Saturn's rings are thousands of miles wide, but only 30 feet thick!"
            )
        )
    )

    val LESSON_ATOM = RobotLesson(
        id = "lesson_atom_chemistry",
        title = "Atoms, Electrons & Chemical Bonds",
        subject = "Atomic Physics & Chemistry",
        description = "Zoom into quantum scales to visualize protons, neutrons, and electron probability clouds.",
        thumbnailIcon = "⚛️",
        cues = listOf(
            RobotSpeechCue(
                id = "cue_a1",
                text = "Everything in the observable universe is constructed from tiny energetic building blocks called atoms.",
                keyword = "atom",
                visualType = HologramVisualType.ATOM_MOLECULE,
                visualTitle = "Rutherford-Bohr Quantum Model",
                visualSubtitle = "Protons, Neutrons & Electron Orbitals",
                visualLabels = listOf("Nucleus (+ Charge)", "Protons (Z Number)", "Neutrons (Mass)", "Electron Orbitals (- Charge)"),
                robotGesture = RobotGesture.POINT_RIGHT,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "An atom is 99.9999999% empty space"
            ),
            RobotSpeechCue(
                id = "cue_a2",
                text = "Electrons orbit the nucleus in discrete energy shells, exchanging or sharing valence electrons to form chemical molecules.",
                keyword = "molecules",
                visualType = HologramVisualType.ATOM_MOLECULE,
                visualTitle = "Valence Shells & Molecular Bonds",
                visualSubtitle = "Covalent, Ionic & Hydrogen Bonds",
                visualLabels = listOf("K, L, M Shells", "Covalent Sharing", "Ionic Transfer", "Octet Stability"),
                robotGesture = RobotGesture.EXPLAINING_HANDS,
                robotEmotion = RobotEmotion.CURIOUS,
                highlightFact = "Chemical bonds store potential energy released in reactions"
            )
        )
    )

    val LESSON_DNA = RobotLesson(
        id = "lesson_dna_genetics",
        title = "DNA & The Molecular Code of Life",
        subject = "Genetics & Molecular Biology",
        description = "Unravel the double helix, base pairing rules, and genetic blueprint of living organisms.",
        thumbnailIcon = "🧬",
        cues = listOf(
            RobotSpeechCue(
                id = "cue_d1",
                text = "Inside the nucleus of every living cell lies DNA: the universal instruction manual of biology.",
                keyword = "DNA",
                visualType = HologramVisualType.DNA_HELIX,
                visualTitle = "Deoxyribonucleic Acid Helix",
                visualSubtitle = "Watson-Crick Double Helix Structure",
                visualLabels = listOf("Sugar-Phosphate Backbone", "Adenine (A)", "Thymine (T)", "Guanine (G)", "Cytosine (C)"),
                robotGesture = RobotGesture.POINT_RIGHT,
                robotEmotion = RobotEmotion.ENTHUSIASTIC,
                highlightFact = "If unwound, the DNA in one human body could stretch to Pluto and back"
            ),
            RobotSpeechCue(
                id = "cue_d2",
                text = "The nitrogenous bases follow strict complementary rules: Adenine always bonds with Thymine, and Cytosine with Guanine.",
                keyword = "bases",
                visualType = HologramVisualType.DNA_HELIX,
                visualTitle = "Complementary Base Pairing",
                visualSubtitle = "A=T (2 Hydrogen Bonds) • C≡G (3 Hydrogen Bonds)",
                visualLabels = listOf("Hydrogen Bonding", "Major & Minor Grooves", "Codons (3 Base Pairs)", "Amino Acid Synthesis"),
                robotGesture = RobotGesture.EXPLAINING_HANDS,
                robotEmotion = RobotEmotion.HAPPY,
                highlightFact = "Humans share 99.9% identical DNA sequences across all individuals"
            )
        )
    )

    val ALL_LESSONS = listOf(
        LESSON_BONES,
        LESSON_HEART,
        LESSON_BRAIN,
        LESSON_SOLAR,
        LESSON_ATOM,
        LESSON_DNA
    )
}
