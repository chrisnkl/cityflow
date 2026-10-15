package com.chrisnkl.cfbackend.application.port.in.report;

import com.chrisnkl.cfbackend.application.command.report.GetUserStatsCommand;
import com.chrisnkl.cfbackend.application.result.report.UserStatsResult;

public interface GetUserStatsUseCase {

    UserStatsResult getUserStats(GetUserStatsCommand command);
}
