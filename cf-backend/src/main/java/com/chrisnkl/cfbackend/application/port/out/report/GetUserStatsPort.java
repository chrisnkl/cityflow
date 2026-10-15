package com.chrisnkl.cfbackend.application.port.out.report;

import com.chrisnkl.cfbackend.application.command.report.GetUserStatsCommand;
import com.chrisnkl.cfbackend.application.result.report.UserStatsResult;

public interface GetUserStatsPort {

    UserStatsResult getUserStats(GetUserStatsCommand command);
}
