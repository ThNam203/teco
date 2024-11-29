package com.worthybitbuilders.squadsense.utils;

import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardRowHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

import java.util.ArrayList;
import java.util.List;

public class ProjectTemplates {
    public static List<BoardContentModel> sampleProjectContent() {
        List<BoardRowHeaderModel> rowTitles = new ArrayList<>();
        rowTitles.add(new BoardRowHeaderModel("Task 1"));
        rowTitles.add(new BoardRowHeaderModel("Task 2"));
        rowTitles.add(new BoardRowHeaderModel("Task 3"));
        rowTitles.add(new BoardRowHeaderModel("Task 4"));

        List<BoardColumnHeaderModel> columnTitles = new ArrayList<>();
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.User, "Person"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Frontend"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Backend"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text, "Note"));

        List<List<BoardBaseItemModel>> cells = new ArrayList<>();

        List<String> statusColors = new ArrayList<>();
        statusColors.add("#ec8f3c");
        statusColors.add("#e43d16");
        statusColors.add("#14c90f");
        statusColors.add("#0f9fc9");
        statusColors.add("#0fc998");
        statusColors.add("#4c0fb5");
        statusColors.add("#b50f9f");
        statusColors.add("#c90f14");
        statusColors.add("#c9a00f");
        statusColors.add("#0fc9c4");
        statusColors.add("#0f79b5");

        List<String> statusContent = new ArrayList<>();
        statusContent.add("Working");
        statusContent.add("Start working");
        statusContent.add("Done");
        statusContent.add("Nearly done");

        List<BoardBaseItemModel> firstRow = new ArrayList<>();
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardStatusItemModel("Working", statusContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Start working", statusContent, statusColors));
        firstRow.add(new BoardTextItemModel(""));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardStatusItemModel("Done", statusContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Working", statusContent, statusColors));
        secondRow.add(new BoardTextItemModel(""));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardStatusItemModel("Nearly done", statusContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Done", statusContent, statusColors));
        thirdRow.add(new BoardTextItemModel(""));

        List<BoardBaseItemModel> fourthRow = new ArrayList<>();
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardStatusItemModel("Nearly done", statusContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Done", statusContent, statusColors));
        fourthRow.add(new BoardTextItemModel(""));

        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);
        cells.add(fourthRow);

        List<BoardContentModel> projectContent = new ArrayList<>();
        projectContent.add(new BoardContentModel("Task board", rowTitles, columnTitles, cells));
        return projectContent;
    }
    public static List<BoardContentModel> sampleITManagementContent(){

        List<BoardRowHeaderModel> rowTitles = new ArrayList<>();
        rowTitles.add(new BoardRowHeaderModel("Employee name 3"));
        rowTitles.add(new BoardRowHeaderModel("Employee name 5"));
        rowTitles.add(new BoardRowHeaderModel("Employee name 1"));
        rowTitles.add(new BoardRowHeaderModel("Employee name 2"));

        List<BoardColumnHeaderModel> columnTitles = new ArrayList<>();
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Update, "Updates"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.User, "IT owner"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.User, "Responsible HR"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Date, "Start date"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Team"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Site"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Computer type"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Computer setup"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Google account"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Zoom account"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "365 account"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Setup desk monitor"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Setup entrance tag"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text, "Email"));

        List<List<BoardBaseItemModel>> cells = new ArrayList<>();

        List<String> statusColors = new ArrayList<>();
        statusColors.add("#ec8f3c");
        statusColors.add("#e43d16");
        statusColors.add("#14c90f");
        statusColors.add("#0f9fc9");
        statusColors.add("#0fc998");
        statusColors.add("#4c0fb5");
        statusColors.add("#b50f9f");
        statusColors.add("#c90f14");
        statusColors.add("#c9a00f");
        statusColors.add("#0fc9c4");
        statusColors.add("#0f79b5");

        List<String> teamContent = new ArrayList<>();
        teamContent.add("Product");
        teamContent.add("HR");
        teamContent.add("Design");
        teamContent.add("R&D");
        teamContent.add("Sales");
        teamContent.add("Partners");
        teamContent.add("Finance");
        teamContent.add("");

        List<String> siteContent = new ArrayList<>();
        siteContent.add("Florida");
        siteContent.add("New York");
        siteContent.add("Denver");
        siteContent.add("");

        List<String> computerTypeContent = new ArrayList<>();
        computerTypeContent.add("PC");
        computerTypeContent.add("Mac");
        computerTypeContent.add("");

        List<String> computerSetupContent = new ArrayList<>();
        computerSetupContent.add("Working on it");
        computerSetupContent.add("Stuck");
        computerSetupContent.add("Done");
        computerSetupContent.add("");

        List<String> googleAccountContent = new ArrayList<>();
        googleAccountContent.add("Working on it");
        googleAccountContent.add("Stuck");
        googleAccountContent.add("Done");
        googleAccountContent.add("");

        List<String> zoomAccountContent = new ArrayList<>();
        zoomAccountContent.add("Working on it");
        zoomAccountContent.add("Stuck");
        zoomAccountContent.add("Done");
        zoomAccountContent.add("");

        List<String> account365Content = new ArrayList<>();
        account365Content.add("Working on it");
        account365Content.add("Stuck");
        account365Content.add("Done");
        account365Content.add("");

        List<String> setupDeskMonitorContent = new ArrayList<>();
        setupDeskMonitorContent.add("Working on it");
        setupDeskMonitorContent.add("Stuck");
        setupDeskMonitorContent.add("Done");
        setupDeskMonitorContent.add("");

        List<String> setupEntranceTagContent = new ArrayList<>();
        setupEntranceTagContent.add("Working on it");
        setupEntranceTagContent.add("Stuck");
        setupEntranceTagContent.add("Done");
        setupEntranceTagContent.add("");

        List<BoardBaseItemModel> firstRow = new ArrayList<>();
        firstRow.add(new BoardUpdateItemModel());
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardDateItemModel(2020,7,23));
        firstRow.add(new BoardStatusItemModel("Product", teamContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Florida", siteContent, statusColors));
        firstRow.add(new BoardStatusItemModel("PC", computerTypeContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Working on it", computerSetupContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Done", googleAccountContent, statusColors));
        firstRow.add(new BoardStatusItemModel("", zoomAccountContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Working on it", account365Content, statusColors));
        firstRow.add(new BoardStatusItemModel("Stuck", setupDeskMonitorContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Working on it", setupEntranceTagContent, statusColors));
        firstRow.add(new BoardTextItemModel("21521010@gm.uit.edu"));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUpdateItemModel());
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardDateItemModel(2020,8,14));
        secondRow.add(new BoardStatusItemModel("Finance", teamContent, statusColors));
        secondRow.add(new BoardStatusItemModel("New York", siteContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Mac", computerTypeContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Stuck", computerSetupContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Done", googleAccountContent, statusColors));
        secondRow.add(new BoardStatusItemModel("", zoomAccountContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Done", account365Content, statusColors));
        secondRow.add(new BoardStatusItemModel("Working on it", setupDeskMonitorContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Done", setupEntranceTagContent, statusColors));
        secondRow.add(new BoardTextItemModel("21520101@gm.uit.edu"));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUpdateItemModel());
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardDateItemModel(2020,8,29));
        thirdRow.add(new BoardStatusItemModel("Partners", teamContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Denver", siteContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("PC", computerTypeContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Done", computerSetupContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Done", googleAccountContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Stuck", zoomAccountContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Done", account365Content, statusColors));
        thirdRow.add(new BoardStatusItemModel("Done", setupDeskMonitorContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Done", setupEntranceTagContent, statusColors));
        thirdRow.add(new BoardTextItemModel(""));

        List<BoardBaseItemModel> fourthRow = new ArrayList<>();
        fourthRow.add(new BoardUpdateItemModel());
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardDateItemModel(2020,9,1));
        fourthRow.add(new BoardStatusItemModel("Sales", teamContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Florida", siteContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Mac", computerTypeContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Done", computerSetupContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Done", googleAccountContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Done", zoomAccountContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Done", account365Content, statusColors));
        fourthRow.add(new BoardStatusItemModel("Done", setupDeskMonitorContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Done", setupEntranceTagContent, statusColors));
        fourthRow.add(new BoardTextItemModel(""));

        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);
        cells.add(fourthRow);

        List<BoardContentModel> content = new ArrayList<>();
        content.add(new BoardContentModel("Main Table", rowTitles, columnTitles, cells));
        return content;
    }
    public static List<BoardContentModel> sampleFacilitiesRequestsContent(){
        List<BoardRowHeaderModel> rowTitles = new ArrayList<>();
        rowTitles.add(new BoardRowHeaderModel("I want to arrange an event"));
        rowTitles.add(new BoardRowHeaderModel("Where can I see the list of our vendors?"));
        rowTitles.add(new BoardRowHeaderModel("Order food for the guests"));

        List<BoardColumnHeaderModel> columnTitles = new ArrayList<>();
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Update,"Updates"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text,"Description"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Date,"Created at"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status,"Priority"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.User,"Assignee"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Date,"Due date"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status,"Status"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status,"Type"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text,"Requestor name"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text,"Requestor email"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text,"Requestor phone"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status,"Department"));

        List<List<BoardBaseItemModel>> cells = new ArrayList<>();

        List<String> statusColors = new ArrayList<>();
        statusColors.add("#ec8f3c");
        statusColors.add("#e43d16");
        statusColors.add("#14c90f");
        statusColors.add("#0f9fc9");
        statusColors.add("#0fc998");
        statusColors.add("#4c0fb5");
        statusColors.add("#b50f9f");
        statusColors.add("#c90f14");
        statusColors.add("#c9a00f");
        statusColors.add("#0fc9c4");
        statusColors.add("#0f79b5");

        List<String> priorityContent = new ArrayList<>();
        priorityContent.add("High");
        priorityContent.add("Medium");
        priorityContent.add("Low");
        priorityContent.add("");

        List<String> statusContent = new ArrayList<>();
        statusContent.add("Working on it");
        statusContent.add("Stuck");
        statusContent.add("Waiting for approval");
        statusContent.add("Done");
        statusContent.add("New");

        List<String> typeContent = new ArrayList<>();
        typeContent.add("Report a maintenance issue");
        typeContent.add("Request a move");
        typeContent.add("Request an event");
        typeContent.add("General question");
        typeContent.add("");

        List<String> departmentContent = new ArrayList<>();
        departmentContent.add("Finance");
        departmentContent.add("Sales");
        departmentContent.add("Partners");
        departmentContent.add("Marketing");
        departmentContent.add("HR");
        departmentContent.add("IT");
        departmentContent.add("Customer Support");
        departmentContent.add("Security");
        departmentContent.add("");

        List<BoardBaseItemModel> firstRow = new ArrayList<>();
        firstRow.add(new BoardUpdateItemModel());
        firstRow.add(new BoardTextItemModel("I need to order food for the guests"));
        firstRow.add(new BoardDateItemModel(2020, 8, 14));
        firstRow.add(new BoardStatusItemModel("High", priorityContent, statusColors));
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardDateItemModel(2020, 8, 14));
        firstRow.add(new BoardStatusItemModel("New", statusContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Report a maintenance issue", typeContent, statusColors));
        firstRow.add(new BoardTextItemModel("John Doe"));
        firstRow.add(new BoardTextItemModel("johndoe@gmail.com"));
        firstRow.add(new BoardTextItemModel("123456789"));
        firstRow.add(new BoardStatusItemModel("Sales", departmentContent, statusColors));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUpdateItemModel());
        secondRow.add(new BoardTextItemModel("I need it for the meetup"));
        secondRow.add(new BoardDateItemModel(2020, 8, 15));
        secondRow.add(new BoardStatusItemModel("Medium", priorityContent, statusColors));
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardDateItemModel());
        secondRow.add(new BoardStatusItemModel("Waiting for approval", statusContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Request a move", typeContent, statusColors));
        secondRow.add(new BoardTextItemModel("Jane Doe"));
        secondRow.add(new BoardTextItemModel("janedoe@gmail.com"));
        secondRow.add(new BoardTextItemModel("123456789"));
        secondRow.add(new BoardStatusItemModel("Marketing", departmentContent, statusColors));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUpdateItemModel());
        thirdRow.add(new BoardTextItemModel("We need it by 9 am"));
        thirdRow.add(new BoardDateItemModel(2020, 8, 16));
        thirdRow.add(new BoardStatusItemModel("Low", priorityContent, statusColors));
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardDateItemModel());
        thirdRow.add(new BoardStatusItemModel("Done", statusContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("General question", typeContent, statusColors));
        thirdRow.add(new BoardTextItemModel("Tom"));
        thirdRow.add(new BoardTextItemModel("tom@gmail.com"));
        thirdRow.add(new BoardTextItemModel("123456789"));
        thirdRow.add(new BoardStatusItemModel("IT", departmentContent, statusColors));


        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);

        List<BoardContentModel> content = new ArrayList<>();
        content.add(new BoardContentModel("Facilities Requests", rowTitles, columnTitles, cells));
        return content;
    }
    public static List<BoardContentModel> sampleProjectRequestAndApprovalsContent() {
        List<BoardRowHeaderModel> rowTitles = new ArrayList<>();
        rowTitles.add(new BoardRowHeaderModel("Project Golf"));
        rowTitles.add(new BoardRowHeaderModel("Project Zulu"));
        rowTitles.add(new BoardRowHeaderModel("Project Juliet"));
        rowTitles.add(new BoardRowHeaderModel("Project Kilo"));

        List<BoardColumnHeaderModel> columnTitles = new ArrayList<>();
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Update,"Updates"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.User,"Initiator"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status,"Request Status"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Date,"Request Date"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text,"Executive Summary"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text,"Anticipated Outcomes"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text,"Recommendation"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Number,"Approved Budget"));

        List<List<BoardBaseItemModel>> cells = new ArrayList<>();

        List<String> statusColors = new ArrayList<>();
        statusColors.add("#ec8f3c");
        statusColors.add("#e43d16");
        statusColors.add("#14c90f");
        statusColors.add("#0f9fc9");
        statusColors.add("#0fc998");
        statusColors.add("#4c0fb5");
        statusColors.add("#b50f9f");
        statusColors.add("#c90f14");
        statusColors.add("#c9a00f");
        statusColors.add("#0fc9c4");
        statusColors.add("#0f79b5");

        List<String> statusContent = new ArrayList<>();
        statusContent.add("New Request");
        statusContent.add("Under Consideration");
        statusContent.add("Approved");
        statusContent.add("Rejected");

        List<BoardBaseItemModel> firstRow = new ArrayList<>();
        firstRow.add(new BoardUpdateItemModel());
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardStatusItemModel("New Request", statusContent, statusColors));
        firstRow.add(new BoardDateItemModel(2020, 8, 14));
        firstRow.add(new BoardTextItemModel("The feasibility study also analyzes in detail the project objectives, performance measures, assumptions, constraints, and alternative options."));
        firstRow.add(new BoardTextItemModel("This technology migration will reduce overhead costs associated with the large workforce currently required to manage these tasks. De-centralized employees will have more autonomy to manage their payroll elections, training, reporting, and various other administrative tasks. The company will also benefit from more timely and accurate financial reporting as a result of our regional managers’ ability to enter and continuously update their financial metrics. This real-time access reduces errors, improves cycle time, and is readily available to any authorized user."));
        firstRow.add(new BoardTextItemModel("Timesheet and payroll data will be immediately accessible for quality control and reporting purposes, which will reduce the need for staff in non-billable positions to gather, analyze, and compile data.\n" +
                "Employees will have the ability to register for training, which reduces the burden on managers and training staff.\n" +
                "\n" +
                "\n" +
                "\n" +
                "Employees will be able to enter and edit their timesheet data at any time from any location instead of phoning their data to their regional manager for entry into the mainframe system.\n" +
                "Timesheet and payroll data will be immediately accessible for quality control and reporting purposes, which will reduce the need for staff in non-billable positions to gather, analyze, and compile data.\n" +
                "Employees will have the ability to register for training, which reduces the burden on managers and training staff."));
        firstRow.add(new BoardNumberItemModel("1000000"));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUpdateItemModel());
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardStatusItemModel("Under Consideration", statusContent, statusColors));
        secondRow.add(new BoardDateItemModel(2020, 8, 15));
        secondRow.add(new BoardTextItemModel("The feasibility study also analyzes in detail the project objectives, performance measures, assumptions, constraints, and alternative options."));
        secondRow.add(new BoardTextItemModel("This technology migration will reduce overhead costs associated with the large workforce currently required to manage these tasks. De-centralized employees will have more autonomy to manage their payroll elections, training, reporting, and various other administrative tasks. The company will also benefit from more timely and accurate financial reporting as a result of our regional managers’ ability to enter and continuously update their financial metrics. This real-time access reduces errors, improves cycle time, and is readily available to any authorized user."));
        secondRow.add(new BoardTextItemModel("Timesheet and payroll data will be immediately accessible for quality control and reporting purposes, which will reduce the need for staff in non-billable positions to gather, analyze, and compile data.\n" +
                "Employees will have the ability to register for training, which reduces the burden on managers and training staff.\n" +
                "\n" +
                "\n" +
                "\n" +
                "Employees will be able to enter and edit their timesheet data at any time from any location instead of phoning their data to their regional manager for entry into the mainframe system.\n" +
                "Timesheet and payroll data will be immediately accessible for quality control and reporting purposes, which will reduce the need for staff in non-billable positions to gather, analyze, and compile data.\n" +
                "Employees will have the ability to register for training, which reduces the burden on managers and training staff."));
        secondRow.add(new BoardNumberItemModel("1000000"));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUpdateItemModel());
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardStatusItemModel("Approved", statusContent, statusColors));
        thirdRow.add(new BoardDateItemModel(2020, 8, 16));
        thirdRow.add(new BoardTextItemModel("The feasibility study also analyzes in detail the project objectives, performance measures, assumptions, constraints, and alternative options."));
        thirdRow.add(new BoardTextItemModel("This technology migration will reduce overhead costs associated with the large workforce currently required to manage these tasks. De-centralized employees will have more autonomy to manage their payroll elections, training, reporting, and various other administrative tasks. The company will also benefit from more timely and accurate financial reporting as a result of our regional managers’ ability to enter and continuously update their financial metrics. This real-time access reduces errors, improves cycle time, and is readily available to any authorized user."));
        thirdRow.add(new BoardTextItemModel("Timesheet and payroll data will be immediately accessible for quality control and reporting purposes, which will reduce the need for staff in non-billable positions to gather, analyze, and compile data.\n" +
                "Employees will have the ability to register for training, which reduces the burden on managers and training staff.\n" +
                "\n" +
                "\n" +
                "\n" +
                "Employees will be able to enter and edit their timesheet data at any time from any location instead of phoning their data to their regional manager for entry into the mainframe system.\n" +
                "Timesheet and payroll data will be immediately accessible for quality control and reporting purposes, which will reduce the need for staff in non-billable positions to gather, analyze, and compile data.\n" +
                "Employees will have the ability to register for training, which reduces the burden on managers and training staff."));
        thirdRow.add(new BoardNumberItemModel("1000000"));

        List<BoardBaseItemModel> fourthRow = new ArrayList<>();
        fourthRow.add(new BoardUpdateItemModel());
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardStatusItemModel("Rejected", statusContent, statusColors));
        fourthRow.add(new BoardDateItemModel(2020, 8, 17));
        fourthRow.add(new BoardTextItemModel("The feasibility study also analyzes in detail the project objectives, performance measures, assumptions, constraints, and alternative options."));
        fourthRow.add(new BoardTextItemModel("This technology migration will reduce overhead costs associated with the large workforce currently required to manage these tasks. De-centralized employees will have more autonomy to manage their payroll elections, training, reporting, and various other administrative tasks. The company will also benefit from more timely and accurate financial reporting as a result of our regional managers’ ability to enter and continuously update their financial metrics. This real-time access reduces errors, improves cycle time, and is readily available to any authorized user."));
        fourthRow.add(new BoardTextItemModel("Timesheet and payroll data will be immediately accessible for quality control and reporting purposes, which will reduce the need for staff in non-billable positions to gather, analyze, and compile data.\n" +
                "Employees will have the ability to register for training, which reduces the burden on managers and training staff.\n" +
                "\n" +
                "\n" +
                "\n" +
                "Employees will be able to enter and edit their timesheet data at any time from any location instead of phoning their data to their regional manager for entry into the mainframe system.\n" +
                "Timesheet and payroll data will be immediately accessible for quality control and reporting purposes, which will reduce the need for staff in non-billable positions to gather, analyze, and compile data.\n" +
                "Employees will have the ability to register for training, which reduces the burden on managers and training staff."));
        fourthRow.add(new BoardNumberItemModel("1000000"));


        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);
        cells.add(fourthRow);

        List<BoardContentModel> content = new ArrayList<>();
        content.add(new BoardContentModel("Project Request & Approvals", rowTitles, columnTitles, cells));
        return content;
    }
    public static List<BoardContentModel> sampleRecuitmenAndOnboardingContent() {
        List<BoardRowHeaderModel> rowTitles = new ArrayList<>();
        rowTitles.add(new BoardRowHeaderModel("Applicant 1"));
        rowTitles.add(new BoardRowHeaderModel("Applicant 2"));
        rowTitles.add(new BoardRowHeaderModel("Applicant 3"));
        rowTitles.add(new BoardRowHeaderModel("Applicant 4"));

        List<BoardColumnHeaderModel> columnTitles = new ArrayList<>();
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Update, "Updates"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.User, "Recruiter"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Date, "Application date"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text, "Role"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Department"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Resume"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Phone interview"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "In-person interview"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "References"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Offer sent"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Contract"));

        List<List<BoardBaseItemModel>> cells = new ArrayList<>();

        List<String> statusColors = new ArrayList<>();
        statusColors.add("#ec8f3c");
        statusColors.add("#e43d16");
        statusColors.add("#14c90f");
        statusColors.add("#0f9fc9");
        statusColors.add("#0fc998");
        statusColors.add("#4c0fb5");
        statusColors.add("#b50f9f");
        statusColors.add("#c90f14");
        statusColors.add("#c9a00f");
        statusColors.add("#0fc9c4");
        statusColors.add("#0f79b5");

        List<String> DepartmentContent = new ArrayList<>();
        DepartmentContent.add("Marketing");
        DepartmentContent.add("Sales");
        DepartmentContent.add("IT");
        DepartmentContent.add("HR");
        DepartmentContent.add("Finance");
        DepartmentContent.add("Legal");
        DepartmentContent.add("Operations");
        DepartmentContent.add("Customer Service");
        DepartmentContent.add("R&D");

        List<String> RoleContent = new ArrayList<>();
        RoleContent.add("Marketing Manager");
        RoleContent.add("Sales Manager");
        RoleContent.add("IT Manager");
        RoleContent.add("HR Manager");
        RoleContent.add("Finance Manager");
        RoleContent.add("Legal Manager");
        RoleContent.add("Operations Manager");
        RoleContent.add("Customer Service Manager");
        RoleContent.add("R&D Manager");

        List<String> ResumeContent = new ArrayList<>();
        ResumeContent.add("Working on it");
        ResumeContent.add("No go");
        ResumeContent.add("Go");
        ResumeContent.add("");

        List<String> PhoneInterviewContent = new ArrayList<>();
        PhoneInterviewContent.add("Working on it");
        PhoneInterviewContent.add("No go");
        PhoneInterviewContent.add("Go");
        PhoneInterviewContent.add("");

        List<String> InPersonInterviewContent = new ArrayList<>();
        InPersonInterviewContent.add("Working on it");
        InPersonInterviewContent.add("No go");
        InPersonInterviewContent.add("Go");
        InPersonInterviewContent.add("");

        List<String> ReferencesContent = new ArrayList<>();
        ReferencesContent.add("Working on it");
        ReferencesContent.add("No go");
        ReferencesContent.add("Go");
        ReferencesContent.add("");

        List<String> OfferSentContent = new ArrayList<>();
        OfferSentContent.add("Working on it");
        OfferSentContent.add("No go");
        OfferSentContent.add("Go");
        OfferSentContent.add("");

        List<String> ContractContent = new ArrayList<>();
        ContractContent.add("Working on it");
        ContractContent.add("No go");
        ContractContent.add("Go");
        ContractContent.add("");


        List<BoardBaseItemModel> firstRow = new ArrayList<>();
        firstRow.add(new BoardUpdateItemModel());
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardDateItemModel(2020, 8, 17));
        firstRow.add(new BoardTextItemModel("Marketing Manager"));
        firstRow.add(new BoardStatusItemModel("Marketing", DepartmentContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Go", ResumeContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Go", PhoneInterviewContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Go", InPersonInterviewContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Go", ReferencesContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Go", OfferSentContent, statusColors));
        firstRow.add(new BoardStatusItemModel("Go", ContractContent, statusColors));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUpdateItemModel());
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardDateItemModel(2020, 8, 17));
        secondRow.add(new BoardTextItemModel("Sales Manager"));
        secondRow.add(new BoardStatusItemModel("Sales", DepartmentContent, statusColors));
        secondRow.add(new BoardStatusItemModel("No go", ResumeContent, statusColors));
        secondRow.add(new BoardStatusItemModel("No go", PhoneInterviewContent, statusColors));
        secondRow.add(new BoardStatusItemModel("No go", InPersonInterviewContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Working on it", ReferencesContent, statusColors));
        secondRow.add(new BoardStatusItemModel("Working on it", OfferSentContent, statusColors));
        secondRow.add(new BoardStatusItemModel("", ContractContent, statusColors));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUpdateItemModel());
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardDateItemModel(2020, 8, 17));
        thirdRow.add(new BoardTextItemModel("IT Manager"));
        thirdRow.add(new BoardStatusItemModel("IT", DepartmentContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("No go", ResumeContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Go", PhoneInterviewContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Working on it", InPersonInterviewContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("", ReferencesContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("Go", OfferSentContent, statusColors));
        thirdRow.add(new BoardStatusItemModel("", ContractContent, statusColors));

        List<BoardBaseItemModel> fourthRow = new ArrayList<>();
        fourthRow.add(new BoardUpdateItemModel());
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardDateItemModel(2020, 8, 17));
        fourthRow.add(new BoardTextItemModel("HR Manager"));
        fourthRow.add(new BoardStatusItemModel("HR", DepartmentContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Working on it", ResumeContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Working on it", PhoneInterviewContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Go", InPersonInterviewContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("", ReferencesContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("", OfferSentContent, statusColors));
        fourthRow.add(new BoardStatusItemModel("Go", ContractContent, statusColors));

        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);
        cells.add(fourthRow);

        List<BoardContentModel> content = new ArrayList<>();
        content.add(new BoardContentModel("Recruitment & On boarding", rowTitles, columnTitles, cells));
        return content;
    }
}
