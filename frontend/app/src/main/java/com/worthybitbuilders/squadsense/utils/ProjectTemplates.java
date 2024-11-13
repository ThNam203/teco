package com.worthybitbuilders.squadsense.utils;

import com.worthybitbuilders.squadsense.models.board_models.BoardBaseItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardColumnHeaderModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardContentModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardDateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardNumberItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardStatusItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardTextItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUpdateItemModel;
import com.worthybitbuilders.squadsense.models.board_models.BoardUserItemModel;

import java.util.ArrayList;
import java.util.List;

public class ProjectTemplates {
    public static List<BoardContentModel> sampleProjectContent() {
        List<String> rowTitles = new ArrayList<>();
        rowTitles.add("Nam");
        rowTitles.add("Dat");
        rowTitles.add("Khoi");
        rowTitles.add("Son");

        List<BoardColumnHeaderModel> columnTitles = new ArrayList<>();
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.User, "Avatar"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Status, "Frontend"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text, "Backend"));
        columnTitles.add(new BoardColumnHeaderModel(BoardColumnHeaderModel.ColumnType.Text, "Fullstack"));

        List<List<BoardBaseItemModel>> cells = new ArrayList<>();

        List<String> statusContent = new ArrayList<>();
        statusContent.add("Lam chua xong");
        statusContent.add("Bat dau lam");
        statusContent.add("Lam gan xong");
        statusContent.add("Da xong");

        List<BoardBaseItemModel> firstRow = new ArrayList<>();
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardStatusItemModel("Lam chua xong", statusContent));
        firstRow.add(new BoardTextItemModel("Bat dau lam"));
        firstRow.add(new BoardTextItemModel("Lam gan xong"));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardStatusItemModel("Da xong", statusContent));
        secondRow.add(new BoardTextItemModel("Lam chua xong"));
        secondRow.add(new BoardTextItemModel("Bat dau lam"));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardStatusItemModel("Lam gan xong", statusContent));
        thirdRow.add(new BoardTextItemModel("Da xong"));
        thirdRow.add(new BoardTextItemModel("Bat dau lam"));

        List<BoardBaseItemModel> fourthRow = new ArrayList<>();
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardStatusItemModel("Lam gan xong", statusContent));
        fourthRow.add(new BoardTextItemModel("Da xong"));
        fourthRow.add(new BoardTextItemModel("Bat dau lam"));

        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);
        cells.add(fourthRow);

        List<BoardContentModel> projectContent = new ArrayList<>();
        projectContent.add(new BoardContentModel("Members", rowTitles, columnTitles, cells));
        return projectContent;
    }
    public static List<BoardContentModel> sampleITManagementContent(){

        List<String> rowTitles = new ArrayList<>();
        rowTitles.add("Employee name 3");
        rowTitles.add("Employee name 5");
        rowTitles.add("Employee name 1");
        rowTitles.add("Employee name 2");

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
        firstRow.add(new BoardStatusItemModel("Product", teamContent));
        firstRow.add(new BoardStatusItemModel("Florida", siteContent));
        firstRow.add(new BoardStatusItemModel("PC", computerTypeContent));
        firstRow.add(new BoardStatusItemModel("Working on it", computerSetupContent));
        firstRow.add(new BoardStatusItemModel("Done", googleAccountContent));
        firstRow.add(new BoardStatusItemModel("", zoomAccountContent));
        firstRow.add(new BoardStatusItemModel("Working on it", account365Content));
        firstRow.add(new BoardStatusItemModel("Stuck", setupDeskMonitorContent));
        firstRow.add(new BoardStatusItemModel("Working on it", setupEntranceTagContent));
        firstRow.add(new BoardTextItemModel("21521010@gm.uit.edu"));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUpdateItemModel());
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardDateItemModel(2020,8,14));
        secondRow.add(new BoardStatusItemModel("Finance", teamContent));
        secondRow.add(new BoardStatusItemModel("New York", siteContent));
        secondRow.add(new BoardStatusItemModel("Mac", computerTypeContent));
        secondRow.add(new BoardStatusItemModel("Stuck", computerSetupContent));
        secondRow.add(new BoardStatusItemModel("Done", googleAccountContent));
        secondRow.add(new BoardStatusItemModel("", zoomAccountContent));
        secondRow.add(new BoardStatusItemModel("Done", account365Content));
        secondRow.add(new BoardStatusItemModel("Working on it", setupDeskMonitorContent));
        secondRow.add(new BoardStatusItemModel("Done", setupEntranceTagContent));
        secondRow.add(new BoardTextItemModel("21520101@gm.uit.edu"));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUpdateItemModel());
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardDateItemModel(2020,8,29));
        thirdRow.add(new BoardStatusItemModel("Partners", teamContent));
        thirdRow.add(new BoardStatusItemModel("Denver", siteContent));
        thirdRow.add(new BoardStatusItemModel("PC", computerTypeContent));
        thirdRow.add(new BoardStatusItemModel("Done", computerSetupContent));
        thirdRow.add(new BoardStatusItemModel("Done", googleAccountContent));
        thirdRow.add(new BoardStatusItemModel("Stuck", zoomAccountContent));
        thirdRow.add(new BoardStatusItemModel("Done", account365Content));
        thirdRow.add(new BoardStatusItemModel("Done", setupDeskMonitorContent));
        thirdRow.add(new BoardStatusItemModel("Done", setupEntranceTagContent));
        thirdRow.add(new BoardTextItemModel(""));

        List<BoardBaseItemModel> fourthRow = new ArrayList<>();
        fourthRow.add(new BoardUpdateItemModel());
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardDateItemModel(2020,9,1));
        fourthRow.add(new BoardStatusItemModel("Sales", teamContent));
        fourthRow.add(new BoardStatusItemModel("Florida", siteContent));
        fourthRow.add(new BoardStatusItemModel("Mac", computerTypeContent));
        fourthRow.add(new BoardStatusItemModel("Done", computerSetupContent));
        fourthRow.add(new BoardStatusItemModel("Done", googleAccountContent));
        fourthRow.add(new BoardStatusItemModel("Done", zoomAccountContent));
        fourthRow.add(new BoardStatusItemModel("Done", account365Content));
        fourthRow.add(new BoardStatusItemModel("Done", setupDeskMonitorContent));
        fourthRow.add(new BoardStatusItemModel("Done", setupEntranceTagContent));
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
        List<String> rowTitles = new ArrayList<>();
        rowTitles.add("I want to arrange an event");
        rowTitles.add("Where can I see the list of our vendors?");
        rowTitles.add("Order food for the guests");

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
        firstRow.add(new BoardDateItemModel(2020,8,14));
        firstRow.add(new BoardStatusItemModel("High", priorityContent));
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardDateItemModel(2020,8,14));
        firstRow.add(new BoardStatusItemModel("New", statusContent));
        firstRow.add(new BoardStatusItemModel("Report a maintenance issue", typeContent));
        firstRow.add(new BoardTextItemModel("John Doe"));
        firstRow.add(new BoardTextItemModel("johndoe@gmail.com"));
        firstRow.add(new BoardTextItemModel("123456789"));
        firstRow.add(new BoardStatusItemModel("Sales", departmentContent));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUpdateItemModel());
        secondRow.add(new BoardTextItemModel("I need it for the meetup"));
        secondRow.add(new BoardDateItemModel(2020,8,15));
        secondRow.add(new BoardStatusItemModel("Medium", priorityContent));
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardDateItemModel());
        secondRow.add(new BoardStatusItemModel("Waiting for approval", statusContent));
        secondRow.add(new BoardStatusItemModel("Request a move", typeContent));
        secondRow.add(new BoardTextItemModel("Jane Doe"));
        secondRow.add(new BoardTextItemModel("janedoe@gmail.com"));
        secondRow.add(new BoardTextItemModel("123456789"));
        secondRow.add(new BoardStatusItemModel("Marketing", departmentContent));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUpdateItemModel());
        thirdRow.add(new BoardTextItemModel("We need it by 9 am"));
        thirdRow.add(new BoardDateItemModel(2020,8,16));
        thirdRow.add(new BoardStatusItemModel("Low", priorityContent));
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardDateItemModel());
        thirdRow.add(new BoardStatusItemModel("Done", statusContent));
        thirdRow.add(new BoardStatusItemModel("General question", typeContent));
        thirdRow.add(new BoardTextItemModel("Tom"));
        thirdRow.add(new BoardTextItemModel("tom@gmail.com"));
        thirdRow.add(new BoardTextItemModel("123456789"));
        thirdRow.add(new BoardStatusItemModel("IT", departmentContent));

        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);

        List<BoardContentModel> content = new ArrayList<>();
        content.add(new BoardContentModel("Facilities Requests", rowTitles, columnTitles, cells));
        return content;
    }
    public static List<BoardContentModel> sampleProjectRequestAndApprovalsContent() {
        List<String> rowTitles = new ArrayList<>();
        rowTitles.add("Project Golf");
        rowTitles.add("Project Zulu");
        rowTitles.add("Project Juliet");
        rowTitles.add("Project Kilo");

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

        List<String> statusContent = new ArrayList<>();
        statusContent.add("New Request");
        statusContent.add("Under Consideration");
        statusContent.add("Approved");
        statusContent.add("Rejected");

        List<BoardBaseItemModel> firstRow = new ArrayList<>();
        firstRow.add(new BoardUpdateItemModel());
        firstRow.add(new BoardUserItemModel());
        firstRow.add(new BoardStatusItemModel("New Request", statusContent));
        firstRow.add(new BoardDateItemModel(2020,8,14));
        firstRow.add(new BoardTextItemModel("The feasibility study also analyzes in detail the project objectives, performance measures, assumptions, constraints and alternative options."));
        firstRow.add(new BoardTextItemModel("This technology migration will reduce overhead costs associated with the large workforce currently required to manage these tasks. De-centralized employees will have more autonomy to manage their payroll elections, training, reporting, and various other administrative tasks. The company will also benefit from more timely and accurate financial reporting as a result of our regional managers’ ability to enter and continuously update their financial metrics. This real time access reduces errors, improves cycle time, and is readily available to any authorized user."));
        firstRow.add(new BoardTextItemModel("Timesheet and payroll data will be immediately accessible for quality control and reporting purposes which will reduce the need for staff in non-billable positions to gather, analyze and compile data\n" +
                "Employees will have the ability to register for training which reduces the burden on managers and training staff\n" +
                "\n" +
                "\n" +
                "\n" +
                "Employees will be able to enter and edit their timesheet data at any time from any location instead of phoning their data to their regional manager for entry into the mainframe system\n" +
                "Timesheet and payroll data will be immediately accessible for quality control and reporting purposes which will reduce the need for staff in non-billable positions to gather, analyze and compile data\n" +
                "Employees will have the ability to register for training which reduces the burden on managers and training staff"));
        firstRow.add(new BoardNumberItemModel("1000000"));

        List<BoardBaseItemModel> secondRow = new ArrayList<>();
        secondRow.add(new BoardUpdateItemModel());
        secondRow.add(new BoardUserItemModel());
        secondRow.add(new BoardStatusItemModel("Under Consideration", statusContent));
        secondRow.add(new BoardDateItemModel(2020,8,15));
        secondRow.add(new BoardTextItemModel("The feasibility study also analyzes in detail the project objectives, performance measures, assumptions, constraints and alternative options."));
        secondRow.add(new BoardTextItemModel("This technology migration will reduce overhead costs associated with the large workforce currently required to manage these tasks. De-centralized employees will have more autonomy to manage their payroll elections, training, reporting, and various other administrative tasks. The company will also benefit from more timely and accurate financial reporting as a result of our regional managers’ ability to enter and continuously update their financial metrics. This real time access reduces errors, improves cycle time, and is readily available to any authorized user."));
        secondRow.add(new BoardTextItemModel("Timesheet and payroll data will be immediately accessible for quality control and reporting purposes which will reduce the need for staff in non-billable positions to gather, analyze and compile data\n" +
                "Employees will have the ability to register for training which reduces the burden on managers and training staff\n" +
                "\n" +
                "\n" +
                "\n" +
                "Employees will be able to enter and edit their timesheet data at any time from any location instead of phoning their data to their regional manager for entry into the mainframe system\n" +
                "Timesheet and payroll data will be immediately accessible for quality control and reporting purposes which will reduce the need for staff in non-billable positions to gather, analyze and compile data\n" +
                "Employees will have the ability to register for training which reduces the burden on managers and training staff"));
        secondRow.add(new BoardNumberItemModel("1000000"));

        List<BoardBaseItemModel> thirdRow = new ArrayList<>();
        thirdRow.add(new BoardUpdateItemModel());
        thirdRow.add(new BoardUserItemModel());
        thirdRow.add(new BoardStatusItemModel("Approved", statusContent));
        thirdRow.add(new BoardDateItemModel(2020,8,16));
        thirdRow.add(new BoardTextItemModel("The feasibility study also analyzes in detail the project objectives, performance measures, assumptions, constraints and alternative options."));
        thirdRow.add(new BoardTextItemModel("This technology migration will reduce overhead costs associated with the large workforce currently required to manage these tasks. De-centralized employees will have more autonomy to manage their payroll elections, training, reporting, and various other administrative tasks. The company will also benefit from more timely and accurate financial reporting as a result of our regional managers’ ability to enter and continuously update their financial metrics. This real time access reduces errors, improves cycle time, and is readily available to any authorized user."));
        thirdRow.add(new BoardTextItemModel("Timesheet and payroll data will be immediately accessible for quality control and reporting purposes which will reduce the need for staff in non-billable positions to gather, analyze and compile data\n" +
                "Employees will have the ability to register for training which reduces the burden on managers and training staff\n" +
                "\n" +
                "\n" +
                "\n" +
                "Employees will be able to enter and edit their timesheet data at any time from any location instead of phoning their data to their regional manager for entry into the mainframe system\n" +
                "Timesheet and payroll data will be immediately accessible for quality control and reporting purposes which will reduce the need for staff in non-billable positions to gather, analyze and compile data\n" +
                "Employees will have the ability to register for training which reduces the burden on managers and training staff"));
        thirdRow.add(new BoardNumberItemModel("1000000"));

        List<BoardBaseItemModel> fourthRow = new ArrayList<>();
        fourthRow.add(new BoardUpdateItemModel());
        fourthRow.add(new BoardUserItemModel());
        fourthRow.add(new BoardStatusItemModel("Rejected", statusContent));
        fourthRow.add(new BoardDateItemModel(2020,8,17));
        fourthRow.add(new BoardTextItemModel("The feasibility study also analyzes in detail the project objectives, performance measures, assumptions, constraints and alternative options."));
fourthRow.add(new BoardTextItemModel("This technology migration will reduce overhead costs associated with the large workforce currently required to manage these tasks. De-centralized employees will have more autonomy to manage their payroll elections, training, reporting, and various other administrative tasks. The company will also benefit from more timely and accurate financial reporting as a result of our regional managers’ ability to enter and continuously update their financial metrics. This real time access reduces errors, improves cycle time, and is readily available to any authorized user."));
        fourthRow.add(new BoardTextItemModel("Timesheet and payroll data will be immediately accessible for quality control and reporting purposes which will reduce the need for staff in non-billable positions to gather, analyze and compile data\n" +
                "Employees will have the ability to register for training which reduces the burden on managers and training staff\n" +
                "\n" +
                "\n" +
                "\n" +
                "Employees will be able to enter and edit their timesheet data at any time from any location instead of phoning their data to their regional manager for entry into the mainframe system\n" +
                "Timesheet and payroll data will be immediately accessible for quality control and reporting purposes which will reduce the need for staff in non-billable positions to gather, analyze and compile data\n" +
                "Employees will have the ability to register for training which reduces the burden on managers and training staff"));
        fourthRow.add(new BoardNumberItemModel("1000000"));

        cells.add(firstRow);
        cells.add(secondRow);
        cells.add(thirdRow);
        cells.add(fourthRow);

        List<BoardContentModel> content = new ArrayList<>();
        content.add(new BoardContentModel("Project Request & Approvals",rowTitles,columnTitles, cells));
        return content;
    }
}
