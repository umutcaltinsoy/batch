State management and restartability

State Management : Restartability in Spring Batch is implemented at two distinct levels: inter-step restartability and inner-step restartability.


Inter-Step Restartability
Inter-step restartability refers to resuming a job from the last failed step, without re-executing the steps that were successfully executed in the previous run.

For instance, if a job is composed of 2 sequential steps and fails at the second step, then the first step won't be re-executed in case of a restart.



Inner-Step Restartability
Inner-step restartability refers to resuming a failed step where it left off, i.e. from within the step itself.

This feature isn't particular to a specific type of steps, but is typically related to chunk-oriented steps. In fact, the chunk-oriented processing model is designed to be tolerant to faults and to restart from the last save point, meaning that successfully processed chunks aren't re-processed again, in the case of a restart.


Error Handling
By default, if an exception occurs in a given step, the step and its enclosing job will fail. At this point, you'll have several choices about how to proceed, depending on the nature of the error:

If the error is transient (like a failed call to a flaky web service), you can decide to restart the job. The job will restart where it left off and might succeed the second time. However, this is not guaranteed as the transient error might happen again. In this case, you'd want to find a way to implement a retry policy around the operation that might fail.
If the error is not transient (like an incorrect input data), then restarting the job won't solve the problem. In this case, you'll need to decide if you want to either fix the problem and restart the job, or to tolerate the bad input and skip it for later analysis or reprocessing.







create table BILLING_DATA
(
DATA_YEAR     INTEGER,
DATA_MONTH    INTEGER,
ACCOUNT_ID    INTEGER,
PHONE_NUMBER  VARCHAR(12),
DATA_USAGE    FLOAT,
CALL_DURATION INTEGER,
SMS_COUNT     INTEGER
);







