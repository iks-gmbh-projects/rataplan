package de.iks.rataplan.dto.restservice;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum NotificationType {
    INVITE("vote/invite"),
    EXPIRE("vote/expire"),
    NEW_PARTICIPANT("vote/participation"),
    PARTICIPATION_INVALIDATED("vote/participation-invalid");
    public final String name;
}
