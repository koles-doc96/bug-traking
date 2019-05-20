package ru.evgen.bugtraking;


import ru.evgen.bugtraking.model.Status;

public class Constant {
    private Constant() {
    }

    public final static String NEW_STATUS = "NEW";
    public final static String WORK_STATUS = "WORK";
    public final static String CLOSE_STATUS = "CLOSE";
    public final static Status newStatus = new Status(1,"Новая");
    public final static Status workStatus = new Status(6,"В работе");
    public final static Status closeStatus = new Status(7,"Закрыта");
}
