package com.campnest.campnest_backend.Controller;

import com.campnest.campnest_backend.Mapper.QuestionnaireAnswerMapper;
import com.campnest.campnest_backend.Mapper.QuestionnaireQuestionMapper;
import com.campnest.campnest_backend.dto.QuestionnaireAnswerDTO;
import com.campnest.campnest_backend.dto.QuestionnaireQuestionDTO;
import com.campnest.campnest_backend.dto.RoommateMatchDTO;
import com.campnest.campnest_backend.model.QuestionnaireAnswer;
import com.campnest.campnest_backend.model.QuestionnaireQuestion;
import com.campnest.campnest_backend.model.User;
import com.campnest.campnest_backend.repository.QuestionnaireAnswerRepository;
import com.campnest.campnest_backend.repository.QuestionnaireQuestionRepository;
import com.campnest.campnest_backend.repository.UserRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/matches")
public class RoommateMatchingController {

    private final UserRepository userRepository;
    private final QuestionnaireQuestionRepository questionRepository;
    private final QuestionnaireAnswerRepository answerRepository;

    public RoommateMatchingController(UserRepository userRepository,
                                      QuestionnaireQuestionRepository questionRepository,
                                      QuestionnaireAnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.questionRepository = questionRepository;
        this.answerRepository = answerRepository;
    }

    /** -------------------- AUTH HELPER -------------------- **/
    private UUID getCurrentUserId() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getPrincipal() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }

        try {
            return UUID.fromString(authentication.getPrincipal().toString());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Invalid user UUID in token");
        }
    }

    /** -------------------- MATCHES -------------------- **/
    @GetMapping
    public ResponseEntity<List<RoommateMatchDTO>> getMatches() {
        UUID userId = getCurrentUserId();
        User currentUser = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        System.out.println("\n🔍 Matching started for user: " + currentUser.getName() + " (" + currentUser.getId() + ")");
        // Get current user's questionnaire answers
        List<QuestionnaireAnswer> currentAnswers = answerRepository.findByUserId(currentUser.getId());
        if (currentAnswers.isEmpty()) {
            System.out.println("⚠️ Current user has not completed questionnaire.");
            return ResponseEntity.ok(Collections.emptyList()); // user must complete questionnaire first
        }

        // Extract key preferences
        String currentSchool = currentUser.getSchool();
        String currentGender = currentUser.getGender();
        String preferredGender = getPreferredGender(currentAnswers);
        boolean currentHasApartment = hasApartment(currentAnswers);
        boolean currentLookingForApartment = isLookingForApartment(currentAnswers);


        System.out.println("🏫 School: " + currentSchool);
        System.out.println("🚻 Gender: " + currentGender + " | Prefers: " + preferredGender);
        System.out.println("🏠 Has Apartment: " + currentHasApartment + " | Looking: " + currentLookingForApartment);


        // Filter valid potential matches
        List<User> filteredUsers = userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(currentUser.getId())) // exclude self
                .filter(u -> !answerRepository.findByUserId(u.getId()).isEmpty()) // must have completed questionnaire
                .filter(u -> {
                    List<QuestionnaireAnswer> otherAnswers = answerRepository.findByUserId(u.getId());
                    if (otherAnswers.isEmpty()){
                        System.out.println("🚫 " + u.getName() + " skipped — no questionnaire answers.");
                        return false;}

                    // 🎓 Same school check
                    if (currentSchool != null && u.getSchool() != null &&
                            !currentSchool.equalsIgnoreCase(u.getSchool())) {
                        System.out.println("🚫 " + u.getName() + " skipped — different school: " + u.getSchool());
                        return false;
                    }

                    // 🚻 Gender filter (match current user's preferred roommate gender)
                    if (preferredGender != null && u.getGender() != null &&
                            !preferredGender.equalsIgnoreCase(u.getGender())) {
                        System.out.println("🚫 " + u.getName() + " skipped — gender mismatch (" + u.getGender() + ")");
                        return false;
                    }

                    // 🏠 Apartment pairing rule: one has, the other is looking
                    boolean otherHasApartment = hasApartment(otherAnswers);
                    boolean otherLooking = isLookingForApartment(otherAnswers);
                    boolean validApartmentPair =
                            (currentHasApartment && otherLooking) ||
                                    (currentLookingForApartment && otherHasApartment);

                    if (!validApartmentPair) {
                        System.out.println("🚫 " + u.getName() + " skipped — incompatible apartment preference.");
                        return false;
                    }

                    System.out.println("✅ " + u.getName() + " passed all filters.");
                    return true;
                })
                .toList();

        // Compute compatibility scores
        List<RoommateMatchDTO> matches = filteredUsers.stream().map(u -> {
                    RoommateMatchDTO dto = new RoommateMatchDTO();
                    dto.setId(u.getId());
                    dto.setName(u.getName());
                    dto.setProfileImage(u.getProfileImage());
                    dto.setAge(u.getAge());
                    dto.setSchool(u.getSchool());
                    dto.setGender(u.getGender());

                    List<QuestionnaireAnswer> otherAnswers = answerRepository.findByUserId(u.getId());
                    int score = computeCompatibility(currentUser, u, currentAnswers, otherAnswers);
                    dto.setCompatibilityScore(score);
                    dto.setCommonInterests(extractCommonInterests(currentAnswers, otherAnswers));
                    System.out.println("💯 " + u.getName() + " compatibility score: " + score);

                    return dto;
                })
                .sorted((a, b) -> b.getCompatibilityScore() - a.getCompatibilityScore())
                .toList();
        System.out.println("✅ Matching complete. Found " + matches.size() + " matches.");
        return ResponseEntity.ok(matches);
    }


    @PostMapping("/recalculate")
    public ResponseEntity<String> recalculateMatches() {
        return ResponseEntity.ok("Matches recalculated successfully!");
    }

    /** -------------------- QUESTIONNAIRE -------------------- **/
    @GetMapping("/questions")
    public ResponseEntity<List<QuestionnaireQuestionDTO>> getQuestions() {
        List<QuestionnaireQuestionDTO> questions = questionRepository.findAll()
                .stream()
                .map(QuestionnaireQuestionMapper::toDTO)
                .toList();
        return ResponseEntity.ok(questions);
    }

    @PostMapping("/answers")
    public ResponseEntity<String> saveAnswers(@RequestBody List<@Valid QuestionnaireAnswerDTO> answersDTO) {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<QuestionnaireAnswer> answers = answersDTO.stream().map(dto -> {
            QuestionnaireQuestion question = questionRepository.findById(dto.getQuestionId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Question not found"));
            return QuestionnaireAnswerMapper.toEntity(dto, user, question);
        }).toList();

        answerRepository.saveAll(answers);
        return ResponseEntity.ok("Answers saved successfully");
    }

    @GetMapping("/answers")
    public ResponseEntity<List<QuestionnaireAnswerDTO>> getUserAnswers() {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        List<QuestionnaireAnswerDTO> answers = answerRepository.findByUserId(user.getId())
                .stream()
                .map(QuestionnaireAnswerMapper::toDTO)
                .toList();

        return ResponseEntity.ok(answers);
    }

    @GetMapping("/status")
    public ResponseEntity<Boolean> hasUserCompletedQuestionnaire() {
        UUID userId = getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        boolean completed = !answerRepository.findByUserId(user.getId()).isEmpty();
        return ResponseEntity.ok(completed);
    }

    /** -------------------- HELPER METHODS -------------------- **/

    private int computeCompatibility(User currentUser, User otherUser,
                                     List<QuestionnaireAnswer> currentAnswers,
                                     List<QuestionnaireAnswer> otherAnswers) {
        int score = 0;

        Map<UUID, List<String>> currentMap = currentAnswers.stream()
                .collect(Collectors.toMap(a -> a.getQuestion().getId(), QuestionnaireAnswer::getAnswers));

        for (QuestionnaireAnswer other : otherAnswers) {
            List<String> current = currentMap.get(other.getQuestion().getId());
            if (current != null) {
                String questionText = other.getQuestion().getQuestion().toLowerCase();

                // 🏠 Apartment pairing logic
                if (questionText.contains("apartment")) {
                    boolean currentHas = current.contains("I have an apartment");
                    boolean otherLooking = other.getAnswers().contains("I'm looking for an apartment");
                    boolean otherHas = other.getAnswers().contains("I have an apartment");
                    boolean currentLooking = current.contains("I'm looking for an apartment");

                    if ((currentHas && otherLooking) || (currentLooking && otherHas)) {
                        score += 40; // strong complementary match
                    }
                    continue;
                }

                // 🎯 Normal shared interest logic
                score += (int) current.stream().filter(other.getAnswers()::contains).count() * 10;
            }
        }

        // 🎓 Same school adds bonus
        if (currentUser.getSchool() != null && currentUser.getSchool().equalsIgnoreCase(otherUser.getSchool())) {
            score += 20;
        }

        // 🚻 Gender preference — prefer same gender
        if (currentUser.getGender() != null && currentUser.getGender().equalsIgnoreCase(otherUser.getGender())) {
            score += 15;
        }

        return score;
    }private boolean hasApartment(List<QuestionnaireAnswer> answers) {
        return answers.stream()
                .anyMatch(a -> a.getQuestion().getQuestion().toLowerCase().contains("apartment") &&
                        a.getAnswers().stream().anyMatch(ans ->
                                ans.toLowerCase().contains("I have an apartment") ||
                                        ans.toLowerCase().contains("own an apartment") ||
                                        ans.toLowerCase().contains("need a roommate")));
    }

    private boolean isLookingForApartment(List<QuestionnaireAnswer> answers) {
        return answers.stream()
                .anyMatch(a -> a.getQuestion().getQuestion().toLowerCase().contains("apartment") &&
                        a.getAnswers().stream().anyMatch(ans ->
                                ans.toLowerCase().contains(" want to move in with someone") ||
                                        ans.toLowerCase().contains("looking for") ||
                                        ans.toLowerCase().contains("I do not have an apartment")));
    }


    private String getPreferredGender(List<QuestionnaireAnswer> answers) {
        return answers.stream()
                .filter(a -> a.getQuestion().getQuestion().toLowerCase().contains("gender of roommate"))
                .flatMap(a -> a.getAnswers().stream())
                .findFirst()
                .orElse(null);
    }


    private List<String> extractCommonInterests(List<QuestionnaireAnswer> currentAnswers, List<QuestionnaireAnswer> otherAnswers) {
        Set<String> common = new HashSet<>();
        for (QuestionnaireAnswer ca : currentAnswers) {
            for (QuestionnaireAnswer oa : otherAnswers) {
                if (ca.getQuestion().getId().equals(oa.getQuestion().getId())) {
                    for (String ans : ca.getAnswers()) {
                        if (oa.getAnswers().contains(ans)) {
                            common.add(ans);
                        }
                    }
                }
            }
        }
        return new ArrayList<>(common);
    }
}
