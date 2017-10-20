package lambda.part3.exercise;

import data.Employee;
import data.JobHistoryEntry;
import data.Person;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;

@SuppressWarnings({"WeakerAccess"})
public class Mapping {

    private static class MapHelper<T> {

        private final List<T> list;

        public MapHelper(List<T> list) {
            this.list = list;
        }

        public List<T> getList() {
            return list;
        }

        // ([T], T -> R) -> [R]
        public <R> MapHelper<R> map(Function<T, R> f) {
            List<R> result = new ArrayList<>();
            for(T t : list)
                result.add(f.apply(t));
            return new MapHelper<>(result);
        }

        // ([T], T -> [R]) -> [R]
        public <R> MapHelper<R> flatMap(Function<T, List<R>> f) {
            List<R> result = new ArrayList<>();
            for(T t : list)
                result.addAll(f.apply(t));
            return new MapHelper<>(result);
        }
    }

    private List<JobHistoryEntry> addOneYear(List<JobHistoryEntry> jhe){
        return jhe.stream()
                .map(e -> e.withDuration(e.getDuration()+1)).collect(Collectors.toList());
    }

    @Test
    public void mapping() {
        List<Employee> employees = getEmployees();

        List<Employee> mappedEmployees = new MapHelper<>(employees)
                /*
                .map(TODO) // Изменить имя всех сотрудников на John .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                .map(TODO) // Добавить всем сотрудникам 1 год опыта .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                .map(TODO) // Заменить все qa на QA
                * */
                .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                .map(e -> e.withJobHistory(
                        e.getJobHistory().stream()
                                .map(x-> "qa".equals(x.getPosition())? x.withPosition("QA") : x)
                                .collect(Collectors.toList())))
                .getList();

        List<Employee> expectedResult = Arrays.asList(
            new Employee(new Person("John", "Galt", 30),
                Arrays.asList(
                        new JobHistoryEntry(3, "dev", "epam"),
                        new JobHistoryEntry(2, "dev", "google")
                )),
            new Employee(new Person("John", "Doe", 40),
                Arrays.asList(
                        new JobHistoryEntry(4, "QA", "yandex"),
                        new JobHistoryEntry(2, "QA", "epam"),
                        new JobHistoryEntry(2, "dev", "abc")
                )),
            new Employee(new Person("John", "White", 50),
                Collections.singletonList(
                        new JobHistoryEntry(6, "QA", "epam")
                ))
        );

        assertEquals(mappedEmployees, expectedResult);
    }

    private static class LazyMapHelper<T, R> {
        List<T> list;
        Function<T,R> function;

        public LazyMapHelper(List<T> list, Function<T, R> function) {
            this.list = list;
            this.function = function;
        }

        public static <T> LazyMapHelper<T, T> from(List<T> list) {
            return new LazyMapHelper<>(list, Function.identity());
        }

        public List<R> force() {
            List<R> result = new ArrayList<>();
            for (T t: list) result.add(function.apply(t));
            return result;
        }

        public <R2> LazyMapHelper<T, R2> map(Function<R, R2> f) {
            return new LazyMapHelper<>(list, function.andThen(f));
        }


    }

    private static class LazyFlatMapHelper<T, R> {

        private final List<T> list;
        private final Function<T, List<R>> mapper;

        private LazyFlatMapHelper(List<T> list, Function<T, List<R>> mapper) {
            this.list = list;
            this.mapper = mapper;
        }

        public static <T> LazyFlatMapHelper<T, T> from(List<T> list) {
            return new LazyFlatMapHelper<>(list, Collections::singletonList);
        }

        public <U> LazyFlatMapHelper<T, U> flatMap(Function<R, List<U>> remapper) {
            return new LazyFlatMapHelper<>(list, mapper.andThen(result -> force(result, remapper)));
        }

        public List<R> force() {
            return force(list, mapper);
        }

        private <A, B> List<B> force(List<A> list, Function<A, List<B>> mapper) {
            List<B> result = new ArrayList<>(list.size());
            list.forEach(element -> result.addAll(mapper.apply(element)));
            return result;
        }
    }

    @Test
    public void lazyFlatMapping() {
        List<Employee> employees = getEmployees();
        List<JobHistoryEntry> force = LazyFlatMapHelper.from(employees)
                                                       .flatMap(Employee::getJobHistory)
                                                       .force();

        List<Character> force1 = LazyFlatMapHelper.from(employees)
                                                  .flatMap(Employee::getJobHistory)
                                                  .flatMap(entry -> entry.getPosition()
                                                                         .chars()
                                                                         .mapToObj(sym -> (char) sym)
                                                                         .collect(Collectors.toList()))
                                                  .force();


        System.out.println();

    }

    @Test
    public void lazyMapping() {
        List<Employee> employees = getEmployees();

        List<Employee> mappedEmployees = LazyMapHelper.from(employees)
                /*
                .map(TODO) // Изменить имя всех сотрудников на John .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                .map(TODO) // Добавить всем сотрудникам 1 год опыта .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                .map(TODO) // Заменить все qu на QA
                */
                .map(e -> e.withPerson(e.getPerson().withFirstName("John")))
                .map(e -> e.withJobHistory(addOneYear(e.getJobHistory())))
                .map(e -> e.withJobHistory(
                        e.getJobHistory().stream()
                                .map(x-> "qa".equals(x.getPosition())? x.withPosition("QA") : x)
                                .collect(Collectors.toList())))
                .force();

        List<Employee> expectedResult = Arrays.asList(
            new Employee(new Person("John", "Galt", 30),
                Arrays.asList(
                        new JobHistoryEntry(3, "dev", "epam"),
                        new JobHistoryEntry(2, "dev", "google")
                )),
            new Employee(new Person("John", "Doe", 40),
                Arrays.asList(
                        new JobHistoryEntry(4, "QA", "yandex"),
                        new JobHistoryEntry(2, "QA", "epam"),
                        new JobHistoryEntry(2, "dev", "abc")
                )),
            new Employee(new Person("John", "White", 50),
                Collections.singletonList(
                        new JobHistoryEntry(6, "QA", "epam")
                ))
        );

        assertEquals(mappedEmployees, expectedResult);
    }

    private List<Employee> getEmployees() {
        return Arrays.asList(
            new Employee(
                new Person("a", "Galt", 30),
                Arrays.asList(
                        new JobHistoryEntry(2, "dev", "epam"),
                        new JobHistoryEntry(1, "dev", "google")
                )),
            new Employee(
                new Person("b", "Doe", 40),
                Arrays.asList(
                        new JobHistoryEntry(3, "qa", "yandex"),
                        new JobHistoryEntry(1, "qa", "epam"),
                        new JobHistoryEntry(1, "dev", "abc")
                )),
            new Employee(
                new Person("c", "White", 50),
                Collections.singletonList(
                        new JobHistoryEntry(5, "qa", "epam")
                ))
        );
    }
}
