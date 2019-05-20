package ru.evgen.bugtraking.utils;

import ru.evgen.bugtraking.model.Status;

import java.lang.reflect.Field;

import static ru.evgen.bugtraking.Constant.*;

public class Util {
    /**
     * Конвертация текстового представления статуса в объект
     *
     * @param str - текстового представления статуса
     * @return - статус
     */
    public static Status getStatus(String str) {
        switch (str) {
            case NEW_STATUS:
                return newStatus;
            case WORK_STATUS:
                return workStatus;
            case CLOSE_STATUS:
                return closeStatus;
            default:
                return null;
        }
    }


    /**
     * Проверка на существования поля в объекте Task
     *
     * @param column - названия поля
     * @return - true - существует. false - не существует
     */
    public static <T> boolean isColumn(String column, T object) {
        Field[] declaredFields = object.getClass().getDeclaredFields();
        for (Field each :
                declaredFields) {
            if (each.getName().equals(column)) {
                return true;
            }
        }
        return false;
    }
}
