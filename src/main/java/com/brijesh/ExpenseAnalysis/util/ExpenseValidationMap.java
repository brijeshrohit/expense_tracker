package com.brijesh.ExpenseAnalysis.util;

import java.util.*;

public class ExpenseValidationMap {

    public static final Map<ExpenseCategory, Set<ExpenseTag>> CATEGORY_TAG_MAP;

    static {
        Map<ExpenseCategory, Set<ExpenseTag>> map = new EnumMap<>(ExpenseCategory.class);

        map.put(ExpenseCategory.FIXED, EnumSet.of(
                ExpenseTag.RENT,
                ExpenseTag.ELECTRICITY,
                ExpenseTag.INTERNET,
                ExpenseTag.COOK,
                ExpenseTag.CLEANER,
                ExpenseTag.DAIRY,
                ExpenseTag.FURNITURE_RENT
        ));

        map.put(ExpenseCategory.VARIABLE, EnumSet.of(
                ExpenseTag.PETROL,
                ExpenseTag.GROCERIES,
                ExpenseTag.ZOMATO_BLINKIT_BIGBASKET,
                ExpenseTag.CHAI_NASHTA,
                ExpenseTag.SHOPPING,
                ExpenseTag.DRY_FRUITS
        ));

        map.put(ExpenseCategory.INVESTMENT, EnumSet.of(
                ExpenseTag.SIP_LONG_TERM,
                ExpenseTag.SIP_SHORT_TERM
        ));

        map.put(ExpenseCategory.INSURANCE, EnumSet.of(
                ExpenseTag.TERM_LIFE
        ));

        map.put(ExpenseCategory.MISCELLANEOUS, EnumSet.of(
                ExpenseTag.TRAIN_TICKET,
                ExpenseTag.FLIGHT_TICKET,
                ExpenseTag.CYLINDER,
                ExpenseTag.GROOMING,
                ExpenseTag.URBAN_CLAP,
                ExpenseTag.SEND_ME,
                ExpenseTag.UDHAR,
                ExpenseTag.CUSTOM // this is flexible and can be added dynamically in UI
        ));

        CATEGORY_TAG_MAP = Collections.unmodifiableMap(map);
    }

    public static boolean isValidTagForCategory(ExpenseCategory category, ExpenseTag tag) {
        return CATEGORY_TAG_MAP.getOrDefault(category, Collections.emptySet()).contains(tag);
    }
}

