package part1.example;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.*;
import static org.junit.Assert.assertEquals;

public class StreamsExample {

    @Test
    public void checkJohnsLastNames() {
        String[] johnsLastNames = getEmployees().stream()
                .map(Employee::getPerson)
                .filter(e -> e.getFirstName().equals("John"))
                .map(Person::getLastName)
                .distinct()
                .toArray(String[]::new);
//                                                    .collect(Collectors.toList());

        assertEquals(Collections.singletonList("Galt"), johnsLastNames);
    }

    @Test
    public void operations() {
        try (Stream<Employee> stream = getEmployees().parallelStream()) {
            Iterator<Employee> iterator = stream.iterator();
        }


        Optional<JobHistoryEntry> jobHistoryEntry =
                getEmployees().stream()
                              .filter(e -> e.getPerson().getFirstName().equals("John"))
                              .map(Employee::getJobHistory)
                              .flatMap(Collection::stream)
                              .peek(System.out::println)
                              .distinct()
                              .sorted(comparing(JobHistoryEntry::getDuration))
                              .skip(1) // long
                              .limit(10) // long
                              .unordered()
                              .parallel()
                              .sequential()
                              .findAny();
        //      .allMatch(Predicate<T>)
        //      .anyMatch(Predicate<T>)
        //      .noneMatch(Predicate<T>)
        //      .reduce(BinaryOperator<T>) // коммутативность операция
        //      .collect(Collector<T, A, R>)
        //      .count()
        //      .findAny()
        //      .findFirst()
        //      .forEach(Consumer<T>)
        //      .forEachOrdered(Consumer<>)
        //      .max()
        //      .min()
        //      .toArray(IntFunction<A[]>)
        //      .iterator()

        // Characteristic :
        // CONCURRENT
        // DISTINCT
        // IMMUTABLE
        // NONNULL
        // ORDERED
        // SIZED
        // SORTED
        // SUBSIZED


        System.out.println(jobHistoryEntry);
    }

    @Test
    public void checkAgedJohnsExpiriences() {
        List<Employee> employees = getEmployees();

        // Every aged (>= 25) John has an odd "dev" job experience

        Comparator<JobHistoryEntry> durationComparator = Comparator.comparingInt(JobHistoryEntry::getDuration);
        employees.stream()
                 .filter(e -> e.getPerson().getFirstName().equals("John"))
                 .filter(e -> e.getPerson().getAge() >= 25)
                 .flatMap(e -> e.getJobHistory().stream())
                 .filter(e -> e.getPosition().equals("dev"))
                 .filter(e -> e.getDuration() % 2 != 0)
                 .distinct()
                 .sorted(durationComparator)
                 .forEachOrdered(System.out::println);
    }

    @Test
    public void getProfessionals() {
        Map<String, Set<Person>> positionIndex = getPositionIndex(getEmployees());

        System.out.println("Developers: ");
        for (Person person : positionIndex.get("dev")) {
            System.out.println(person);
        }

        System.out.println("QA: ");
        for (Person person : positionIndex.get("QA")) {
            System.out.println(person);
        }

        System.out.println("BA: ");
        positionIndex.get("BA").forEach(System.out::println);
    }

    private static Stream<PersonPositionPair> employeeToPairs(Employee employee) {
        return employee.getJobHistory()
                       .stream()
                       .map(JobHistoryEntry::getPosition)
                       .map(position -> new PersonPositionPair(employee.getPerson(), position));
    }

    // [ (John, [dev, QA]), (Bob, [QA, QA])] -> [dev -> [John], QA -> [John, Bob]]
    // [ (John, dev), (John, QA), (Bob, QA), (Bob, QA)] -> [dev -> [John], QA -> [John, Bob]]


    // dev -> (John, dev)
    // QA -> (Jogn, QA), (Bob, QA)


    // dev -> (John)
    // QA -> (Jogn, Bob)
    private Map<String, Set<Person>> getPositionIndex(List<Employee> employees) {
        Stream<PersonPositionPair> personPositionPairStream = employees.stream()
                                                                       .flatMap(StreamsExample::employeeToPairs);

        // Reduce with seed
//        return personPositionPairStream.reduce(Collections.emptyMap(), StreamsExample::addToMap, StreamsExample::combineMaps);

//        return personPositionPairStream
//                .collect(
//                        HashMap::new,
//                        (m, p) -> {
//                            Set<Person> set = m.computeIfAbsent(p.getPosition(), (k) -> new HashSet<>());
//                            set.add(p.getPerson());
//                        },
//                        (m1, m2) -> {
//                            for (Map.Entry<String, Set<Person>> entry : m2.entrySet()) {
//                                Set<Person> set = m1.computeIfAbsent(entry.getKey(), (k) -> new HashSet<>());
//                                set.addAll(entry.getValue());
//                            }
//                        });
        return personPositionPairStream.collect(groupingBy(PersonPositionPair::getPosition, mapping(PersonPositionPair::getPerson, toSet())));
    }

    private static Map<String, Set<Person>> combineMaps(Map<String, Set<Person>> u1, Map<String, Set<Person>> u2) {
        HashMap<String, Set<Person>> result = new HashMap<>(u1);
        for (Map.Entry<String, Set<Person>> entry : u2.entrySet()) {
            result.merge(entry.getKey(), entry.getValue(), (people, people2) -> {
                people.addAll(people2);
                return people;
            });
        }
        return result;
    }

    private static Map<String, Set<Person>> addToMap(Map<String, Set<Person>> origin, PersonPositionPair pair) {
        HashMap<String, Set<Person>> result = new HashMap<>(origin);

        Set<Person> set = result.computeIfAbsent(pair.getPosition(), (k) -> new HashSet<>());
        set.add(pair.getPerson());


//        Map<String, Integer> someMap = new HashMap<>();
//        someMap.put("1", 1);
//        someMap.put("2", 2);
//        someMap.put("3", 3);
//        someMap.put("4", 4);
//
//
//        someMap.put("2", 10);
//        someMap.merge("2", 10, Integer::sum);


        result.merge(pair.getPosition(), Collections.singleton(pair.getPerson()), (oldValue, newValue) -> {
            oldValue.add(pair.getPerson());
            return oldValue;
        });


        return result;
    }


    @Test
    public void getTheCoolestOne() {
        Map<String, Person> coolestByPosition = getCoolestByPosition(getEmployees());

        coolestByPosition.forEach((position, person) -> System.out.println(position + " -> " + person));
    }

    private static class PersonPositionDuration {
        private final Person person;
        private final String position;
        private final int duration;

        public PersonPositionDuration(Person person, String position, int duration) {
            this.person = person;
            this.position = position;
            this.duration = duration;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }

        public int getDuration() {
            return duration;
        }
    }

    private Map<String, Person> getCoolestByPosition(List<Employee> employees) {
        Stream<PersonPositionDuration> personPositionDurationStream = employees.stream()
                .flatMap(
                        e -> e.getJobHistory()
                                .stream()
                                .map(j -> new PersonPositionDuration(e.getPerson(), j.getPosition(), j.getDuration())));
//        final Map<String, PersonPositionDuration> collect = personPositionDurationStream
//                .collect(toMap(
//                        PersonPositionDuration::getPosition,
//                        Function.identity(),
//                        (p1, p2) -> p1.getDuration() > p2.getDuration() ? p1 : p2));
        return personPositionDurationStream
                .collect(groupingBy(
                        PersonPositionDuration::getPosition,
                        collectingAndThen(maxBy(comparing(PersonPositionDuration::getDuration)), p -> p.isPresent() ? p.get().getPerson() : null)));
    }

    @Test
    public void intStream_bad1() {
        int sumDuration =
                getEmployees().stream()
                        .flatMap(
                                employee -> employee.getJobHistory().stream()
                        )
                        .collect(mapping(JobHistoryEntry::getDuration, Collectors.reducing(0, (a, b) -> a + b)));

        System.out.println("sum: " + sumDuration);
    }

    @Test
    public void intStream_bad2() {
        int sumDuration =
                getEmployees().stream()
                        .flatMap(
                                employee -> employee.getJobHistory().stream()
                        )
                        .map(JobHistoryEntry::getDuration)
                        .collect(Collectors.reducing(0, (a, b) -> a + b));

        System.out.println("sum: " + sumDuration);
    }

    @Test
    public void intStream_bad3() {
        int sumDuration =
                getEmployees().stream()
                        .flatMap(
                                employee -> employee.getJobHistory().stream()
                        )
                        .collect(Collectors.summingInt(JobHistoryEntry::getDuration));

        System.out.println("sum: " + sumDuration);
    }

    @Test
    public void intStream() {
        final int sumDuration =
                getEmployees().stream()
                        .flatMap(employee -> employee.getJobHistory().stream())
                        .mapToInt(JobHistoryEntry::getDuration)
                        .sum();

        System.out.println("sum: " + sumDuration);
    }

    @Test
    public void intStream_array() {
        final Employee[] employeesArray = getEmployees().toArray(new Employee[0]);
        //final Employee[] employeesArray = getEmployees().stream().toArray(Employee[]::new);
        final int sumDuration =
                Arrays.stream(employeesArray)
                        .flatMap(employee -> employee.getJobHistory().stream())
                        .mapToInt(JobHistoryEntry::getDuration)
                        .sum();

        System.out.println("sum: " + sumDuration);
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
                new Employee(
                        new Person("John", "Galt", 20),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 21),
                        Arrays.asList(
                                new JobHistoryEntry(4, "BA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 22),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 23),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(2, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 24),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "BA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 25),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 26),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("Bob", "Doe", 27),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(2, "dev", "abc")
                        )),
                new Employee(
                        new Person("John", "White", 28),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "BA", "epam")
                        )),
                new Employee(
                        new Person("John", "Galt", 29),
                        Arrays.asList(
                                new JobHistoryEntry(3, "dev", "epam"),
                                new JobHistoryEntry(1, "dev", "google")
                        )),
                new Employee(
                        new Person("John", "Doe", 30),
                        Arrays.asList(
                                new JobHistoryEntry(4, "QA", "yandex"),
                                new JobHistoryEntry(2, "QA", "epam"),
                                new JobHistoryEntry(5, "dev", "abc")
                        )),
                new Employee(
                        new Person("Bob", "White", 31),
                        Collections.singletonList(
                                new JobHistoryEntry(6, "QA", "epam")
                        ))
        );
    }

    private static class PersonPositionPair {
        private final Person person;
        private final String position;

        public PersonPositionPair(Person person, String position) {
            this.person = person;
            this.position = position;
        }

        public Person getPerson() {
            return person;
        }

        public String getPosition() {
            return position;
        }
    }

}
