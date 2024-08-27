package com.performance.test.iteratorVsForloop;

import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;

import java.time.LocalDate;
import java.util.*;

@Service
public class ListStructureTestService {
    private static final int NUM_ELEMENTS = 100;
    private static final Random RANDOM = new Random();

    public void testPerformance() {
        List<BusinessSchedule> businessScheduleList = createTestData();

        // JIT 컴파일러 워밍업을 위한 워밍업 루프
        for (int i = 0; i < 10; i++) {
            runTestWithForLoop(new ArrayList<>(businessScheduleList));
        }

        StopWatch stopWatch = new StopWatch("Performance Testing");


        // ArrayList 테스트
        runTestWithIterator(new ArrayList<>(businessScheduleList));
        measureMemoryAndTime("ArrayList Iterator", () -> runTestWithIterator(new ArrayList<>(businessScheduleList)), stopWatch);
        measureMemoryAndTime("ArrayList For Loop", () -> runTestWithForLoop(new ArrayList<>(businessScheduleList)), stopWatch);
        measureMemoryAndTime("ArrayList Enhanced For Loop", () -> runTestWithEnhancedForLoop(new ArrayList<>(businessScheduleList)), stopWatch);
        measureMemoryAndTime("ArrayList ListIterator", () -> runTestWithListIterator(new ArrayList<>(businessScheduleList)), stopWatch);
        measureMemoryAndTime("ArrayList Stream", () -> runTestWithStream(new ArrayList<>(businessScheduleList)), stopWatch);

        // LinkedList 테스트
        measureMemoryAndTime("LinkedList Iterator", () -> runTestWithIterator(new LinkedList<>(businessScheduleList)), stopWatch);
        measureMemoryAndTime("LinkedList For Loop", () -> runTestWithForLoop(new LinkedList<>(businessScheduleList)), stopWatch);
        measureMemoryAndTime("LinkedList Enhanced For Loop", () -> runTestWithEnhancedForLoop(new LinkedList<>(businessScheduleList)), stopWatch);
        measureMemoryAndTime("LinkedList ListIterator", () -> runTestWithListIterator(new LinkedList<>(businessScheduleList)), stopWatch);
        measureMemoryAndTime("LinkedList Stream", () -> runTestWithStream(new LinkedList<>(businessScheduleList)), stopWatch);

        System.out.println(stopWatch.prettyPrint());

        calculateAndPrintBlockTypeDistribution(businessScheduleList);
    }

    private void measureMemoryAndTime(String taskName, Runnable task, StopWatch stopWatch) {
        Runtime runtime = Runtime.getRuntime();

        // 메모리 측정 전 GC 호출
        runtime.gc();

        // 시작 메모리 측정
        long beforeMemory = runtime.totalMemory() - runtime.freeMemory();

        stopWatch.start(taskName);
        task.run();
        stopWatch.stop();

        // 끝난 후 메모리 측정
        long afterMemory = runtime.totalMemory() - runtime.freeMemory();

        long memoryUsed = afterMemory - beforeMemory;

        System.out.println(taskName + " used memory: " + memoryUsed + " bytes");
    }

    // 분포 출력 메서드
    private void calculateAndPrintBlockTypeDistribution(List<BusinessSchedule> businessSchedules) {
        MonthlyScheduleDTO monthlyScheduleDTO = new MonthlyScheduleDTO(businessSchedules);

        List<MonthlyScheduleDTO.BusinessScheduleWithBlockTypeDTO> arrayListResult = monthlyScheduleDTO.createScheduleDTOsUsingIterator(new ArrayList<>(businessSchedules));
        List<MonthlyScheduleDTO.BusinessScheduleWithBlockTypeDTO> linkedListResult = monthlyScheduleDTO.createScheduleDTOsUsingIterator(new LinkedList<>(businessSchedules));

        printBlockTypeDistribution("ArrayList Distribution", getBlockTypeDistribution(arrayListResult));
        printBlockTypeDistribution("LinkedList Distribution", getBlockTypeDistribution(linkedListResult));
    }

    private void printBlockTypeDistribution(String methodName, EnumMap<BlockType, Integer> distribution) {
        System.out.println("BlockType Distribution for " + methodName + ":");
        for (EnumMap.Entry<BlockType, Integer> entry : distribution.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
        System.out.println();
    }

    private EnumMap<BlockType, Integer> getBlockTypeDistribution(List<MonthlyScheduleDTO.BusinessScheduleWithBlockTypeDTO> scheduleDTOs) {
        EnumMap<BlockType, Integer> distribution = new EnumMap<>(BlockType.class);

        // 모든 BlockType을 초기화하여 0으로 설정
        for (BlockType blockType : BlockType.values()) {
            distribution.put(blockType, 0);
        }

        // 각 BlockType의 발생 빈도수를 계산하여 증가
        for (MonthlyScheduleDTO.BusinessScheduleWithBlockTypeDTO dto : scheduleDTOs) {
            distribution.put(dto.blockType(), distribution.get(dto.blockType()) + 1);
        }

        return distribution;
    }
    private List<BusinessSchedule> createTestData() {
        List<BusinessSchedule> businessSchedules = new ArrayList<>();
        LocalDate currentDate = LocalDate.now();

        String previousType = null;
        String previousDescription = null;

        for (int i = 0; i < NUM_ELEMENTS; i++) {
            // 날짜를 1일 또는 2일씩 랜덤하게 증가
            int daysToAdd = RANDOM.nextInt(2) + 1; // 1 또는 2
            if (i != 0) {
                currentDate = currentDate.plusDays(daysToAdd);
            }

            // 30% 확률로 이전 Type과 Description 유지
            if (previousType != null && previousDescription != null && RANDOM.nextInt(100) < 30) {
                businessSchedules.add(new BusinessSchedule(previousType, previousDescription, currentDate));
            } else {
                // 70% 확률로 새로운 Type과 Description 생성
                String newType = "Type" + RANDOM.nextInt(3); // Type0, Type1, Type2
                String newDescription = "Description" + RANDOM.nextInt(5); // Description0 ~ Description4

                businessSchedules.add(new BusinessSchedule(newType, newDescription, currentDate));

                // 현재 값을 이전 값으로 저장
                previousType = newType;
                previousDescription = newDescription;
            }
        }
        return businessSchedules;
    }

    // Iterator
    private void runTestWithIterator(List<BusinessSchedule> businessSchedules) {
        MonthlyScheduleDTO monthlyScheduleDTO = new MonthlyScheduleDTO(businessSchedules);
        List<MonthlyScheduleDTO.BusinessScheduleWithBlockTypeDTO> result = monthlyScheduleDTO.createScheduleDTOsUsingIterator(businessSchedules);
    }

    // For 루프
    private void runTestWithForLoop(List<BusinessSchedule> businessSchedules) {
        MonthlyScheduleDTO monthlyScheduleDTO = new MonthlyScheduleDTO(businessSchedules);
        List<MonthlyScheduleDTO.BusinessScheduleWithBlockTypeDTO> result = monthlyScheduleDTO.createScheduleDTOsUsingForLoop(businessSchedules);
    }

    // 향상된 for 문
    private void runTestWithEnhancedForLoop(List<BusinessSchedule> businessSchedules) {
        MonthlyScheduleDTO monthlyScheduleDTO = new MonthlyScheduleDTO(businessSchedules);
        monthlyScheduleDTO.createScheduleDTOsUsingEnhancedForLoop(businessSchedules);
    }

    // ListIterator
    private void runTestWithListIterator(List<BusinessSchedule> businessSchedules) {
        MonthlyScheduleDTO monthlyScheduleDTO = new MonthlyScheduleDTO(businessSchedules);
        monthlyScheduleDTO.createScheduleDTOsUsingListIterator(businessSchedules);
    }

    // Stream
    private void runTestWithStream(List<BusinessSchedule> businessSchedules) {
        MonthlyScheduleDTO monthlyScheduleDTO = new MonthlyScheduleDTO(businessSchedules);
        monthlyScheduleDTO.createScheduleDTOsUsingStream(businessSchedules);
    }



}
