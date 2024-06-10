//package com.altinsoy.batch;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.batch.core.StepContribution;
//import org.springframework.batch.core.scope.context.ChunkContext;
//import org.springframework.batch.core.step.tasklet.Tasklet;
//import org.springframework.batch.repeat.RepeatStatus;
//
//@RequiredArgsConstructor
//public class InsertDbTasklet implements Tasklet {
//
//    private final UserRepository userRepository;
//    @Override
//    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
//
//
//
//        return RepeatStatus.FINISHED;
//    }
//}
