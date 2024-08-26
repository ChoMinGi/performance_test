package com.performance.test.iteratorVsForloop;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class MonthlyScheduleDTO {
    private final List<BusinessSchedule> businessScheduleDTOList;

    public MonthlyScheduleDTO(List<BusinessSchedule> businessScheduleDTOList) {
        this.businessScheduleDTOList = businessScheduleDTOList;
    }

    record BusinessScheduleWithBlockTypeDTO(int day, String changeType, String description, BlockType blockType) {
        public static BusinessScheduleWithBlockTypeDTO from(BusinessSchedule businessSchedule, BlockType blockType) {
            return new BusinessScheduleWithBlockTypeDTO(
                    businessSchedule.getDate().getDayOfMonth(),
                    businessSchedule.getChangeType(),
                    businessSchedule.getDescription(),
                    blockType
            );
        }
    }

    // Iterator를 사용하는 메서드
    public List<BusinessScheduleWithBlockTypeDTO> createScheduleDTOsUsingIterator(List<BusinessSchedule> businessSchedules) {
        List<BusinessScheduleWithBlockTypeDTO> scheduleDTOs = new ArrayList<>();
        Iterator<BusinessSchedule> iterator = businessSchedules.iterator();

        BusinessSchedule previous = null;
        boolean isPreviousLinked = false;

        while (iterator.hasNext()) {
            BusinessSchedule current = iterator.next();

            if (previous != null) {
                boolean isNextLinked = previous.getDate().plusDays(1).isEqual(current.getDate()) &&
                        previous.getChangeType().equals(current.getChangeType()) &&
                        previous.getDescription().equals(current.getDescription());

                BlockType blockType = getBlockType(isPreviousLinked, isNextLinked);
                scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(previous, blockType));

                isPreviousLinked = isNextLinked;
            }
            previous = current;
        }
            // 마지막 요소 처리
        if (previous != null) {
            BlockType lastBlockType = isPreviousLinked ? BlockType.END : BlockType.SINGLE;
            scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(previous, lastBlockType));
        }
            return scheduleDTOs;
    }

    // For 루프를 사용하는 메서드
    public List<BusinessScheduleWithBlockTypeDTO> createScheduleDTOsUsingForLoop(List<BusinessSchedule> businessSchedules) {
        List<BusinessScheduleWithBlockTypeDTO> scheduleDTOs = new ArrayList<>();

        if (businessSchedules.isEmpty()) {
            return scheduleDTOs; // 빈 리스트 처리
        }

        BusinessSchedule current = businessSchedules.get(0);
        boolean isPreviousLinked = false;

        for (int i = 1; i < businessSchedules.size(); i++) {
            BusinessSchedule next = businessSchedules.get(i);

            boolean isNextLinked = current.getDate().plusDays(1).isEqual(next.getDate()) &&
                    current.getChangeType().equals(next.getChangeType()) &&
                    current.getDescription().equals(next.getDescription());

            BlockType blockType = getBlockType(isPreviousLinked, isNextLinked);

            scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(current, blockType));
            isPreviousLinked = isNextLinked;
            current = next;
        }

        // 마지막 일정 처리
        BlockType lastBlockType = isPreviousLinked ? BlockType.END : BlockType.SINGLE;
        scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(current, lastBlockType));

        return scheduleDTOs;
    }

    // 향상된 for 문을 사용한 메서드
    public List<BusinessScheduleWithBlockTypeDTO> createScheduleDTOsUsingEnhancedForLoop(List<BusinessSchedule> businessSchedules) {
        List<BusinessScheduleWithBlockTypeDTO> scheduleDTOs = new ArrayList<>();
        BusinessSchedule previous = null;
        boolean isPreviousLinked = false;

        for (BusinessSchedule current : businessSchedules) {
            if (previous != null) {
                boolean isNextLinked = previous.getDate().plusDays(1).isEqual(current.getDate()) &&
                        previous.getChangeType().equals(current.getChangeType()) &&
                        previous.getDescription().equals(current.getDescription());

                BlockType blockType = getBlockType(isPreviousLinked, isNextLinked);
                scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(previous, blockType));

                isPreviousLinked = isNextLinked;
            }
            previous = current;
        }

        // 마지막 요소 처리
        if (previous != null) {
            BlockType lastBlockType = isPreviousLinked ? BlockType.END : BlockType.SINGLE;
            scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(previous, lastBlockType));
        }

        return scheduleDTOs;
    }

    // ListIterator를 사용한 메서드
    public List<BusinessScheduleWithBlockTypeDTO> createScheduleDTOsUsingListIterator(List<BusinessSchedule> businessSchedules) {
        List<BusinessScheduleWithBlockTypeDTO> scheduleDTOs = new ArrayList<>();
        ListIterator<BusinessSchedule> iterator = businessSchedules.listIterator();

        BusinessSchedule previous = null;
        boolean isPreviousLinked = false;

        while (iterator.hasNext()) {
            BusinessSchedule current = iterator.next();

            if (previous != null) {
                boolean isNextLinked = previous.getDate().plusDays(1).isEqual(current.getDate()) &&
                        previous.getChangeType().equals(current.getChangeType()) &&
                        previous.getDescription().equals(current.getDescription());

                BlockType blockType = getBlockType(isPreviousLinked, isNextLinked);
                scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(previous, blockType));

                isPreviousLinked = isNextLinked;
            }

            previous = current;
        }
        // 마지막 요소 처리
        if (previous != null) {
            BlockType lastBlockType = isPreviousLinked ? BlockType.END : BlockType.SINGLE;
            scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(previous, lastBlockType));
        }

        return scheduleDTOs;
    }

    // Stream 을 이용한 메서드
    public List<BusinessScheduleWithBlockTypeDTO> createScheduleDTOsUsingStream(List<BusinessSchedule> businessSchedules) {
        List<BusinessScheduleWithBlockTypeDTO> scheduleDTOs = new ArrayList<>();
        AtomicReference<BusinessSchedule> previousRef = new AtomicReference<>(null);
        AtomicReference<Boolean> isPreviousLinkedRef = new AtomicReference<>(false);

        businessSchedules.stream().forEach(current -> {
            BusinessSchedule previous = previousRef.get();

            if (previous != null) {
                boolean isNextLinked = previous.getDate().plusDays(1).isEqual(current.getDate()) &&
                        previous.getChangeType().equals(current.getChangeType()) &&
                        previous.getDescription().equals(current.getDescription());

                BlockType blockType = getBlockType(isPreviousLinkedRef.get(), isNextLinked);
                scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(previous, blockType));

                isPreviousLinkedRef.set(isNextLinked);
            }

            previousRef.set(current);
        });

        // 마지막 요소 처리
        BusinessSchedule lastSchedule = previousRef.get();
        if (lastSchedule != null) {
            BlockType lastBlockType = isPreviousLinkedRef.get() ? BlockType.END : BlockType.SINGLE;
            scheduleDTOs.add(BusinessScheduleWithBlockTypeDTO.from(lastSchedule, lastBlockType));
        }

        return scheduleDTOs;
    }




    private static BlockType getBlockType(boolean isPreviousLinked, boolean isNextLinked) {
        BlockType blockType;
        if (isPreviousLinked && isNextLinked) {
            blockType = BlockType.MIDDLE;
        } else if (isPreviousLinked) {
            blockType = BlockType.END;
        } else if (isNextLinked) {
            blockType = BlockType.START;
        } else {
            blockType = BlockType.SINGLE;
        }
        return blockType;
    }
}