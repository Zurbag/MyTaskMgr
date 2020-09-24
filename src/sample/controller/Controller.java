package sample.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.sourse.ConnectionBD;
import sample.sourse.DateStringParser;
import sample.sourse.Task;

import java.io.IOException;
import java.sql.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class Controller {

    //Сождание новой Задачи
    @FXML
    private DatePicker setNewTaskDataFinish;
    @FXML
    private TextField setNewTaskTextField;
    @FXML
    private Button addNewTaskInBD;

    String setNewTaskDataFinishString; //Тут будет храниться дата крайнего срока задачи

    //Поле фильтров
    @FXML
    private Button todayTasks;
    @FXML
    private Button weekTasks;
    @FXML
    private Button laterTasks;
    @FXML
    private Label getIdLabel;
    @FXML
    private Button showAllTasks;

    @FXML
    private Button editButton;


    //ПОЛЯ ТАБЛИЦЫ
    @FXML
    private TableView<Task> tableTask;
    @FXML
    private TableColumn<Task, Button> setStatus;
    @FXML
    private TableColumn<Task, String> name;
    @FXML
    private TableColumn<Task, String> dateCreate;
    @FXML
    private TableColumn<Task, String> dateFinish;

    // ФУТЕР
    @FXML
    private Button deleteButton;

    //Переменная для хранненеия типа кнопки задач todayType, weekType, laterType;
    //В зависимости от того какое значение подставляется так и происходит удаление после установки задачи
    //Заменит на пречисление позже
    String timePeriodTypeTasks = "todayType";

    //Переменная для храннения ID выбраной задачи
    int idTask;
    //Тут храняться задачи
    public ObservableList<Task> tasksList = FXCollections.observableArrayList();

    //ИНИЦИАЛИЗАЦИЯ ПОЛЕЙ ТАБЛИЦЫ
    @FXML
    private void initialize() {
        //Очищаю данные перед показом
        tasksList.clear();
        //Показываю сегодняшние задачи
        initTodayTasks();
        //Устанавливаю в датапикер сегодняшнюю дату, это не влияет на код переджаваемый в базу
        setNewTaskDataFinish.setPromptText(new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("uuuu-MM-d")));
        // устанавливаем тип и значение которое должно хранится в колонке

        setStatus.setCellValueFactory(new PropertyValueFactory<Task, Button>("setStatus"));
        name.setCellValueFactory(new PropertyValueFactory<Task, String>("name"));
        dateCreate.setCellValueFactory(new PropertyValueFactory<Task, String>("dateCreate"));
        dateFinish.setCellValueFactory(new PropertyValueFactory<Task, String>("dateFinish"));

        // заполняем таблицу данными
        tableTask.setItems(tasksList);

        // Получение данных из таблицы в переменную idTask;
        TableView.TableViewSelectionModel<Task> selectionModel = tableTask.getSelectionModel();
        selectionModel.selectedItemProperty().addListener(new ChangeListener<Task>() {

            @Override
            public void changed(ObservableValue<? extends Task> observableValue, Task task, Task t1) {
                if (t1 != null) idTask = t1.getId();
            }
        });


    }

    //УСТАНОВИТЬ ДАТУ НОВОЙ ЗАДАЧИ
    @FXML
    void setNewTaskDataFinish(ActionEvent event) {
        setNewTaskDataFinishString = setNewTaskDataFinish.getValue().toString();
    }

    //СОЗДАНИЕ НОВОЙ ЗАДАЧИ И ДОБАВЛЕНИЕ В БАЗУ
    @FXML
    void addNewTaskInBDClick(ActionEvent event) {


        String nameTask = null;
        if (setNewTaskTextField.getText().equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Ошибка");
            alert.setHeaderText(null);
            alert.setContentText("Поле текст задачи должно быть заполнено");
            alert.showAndWait();
            return;
        } else {
            nameTask = setNewTaskTextField.getText();
        }

        String dataCreate = new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("uuuu-MM-d"));

        String dataFinish;
        if (setNewTaskDataFinishString == null) {
            dataFinish = new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("uuuu-MM-d"));
        } else {
            dataFinish = setNewTaskDataFinishString;
        }

        Task task = new Task(true, nameTask, dataCreate, dataFinish);
        tasksList.add(task);
        try {
            ConnectionBD connectionBD = new ConnectionBD();
            Class.forName(connectionBD.getDRIVER()).newInstance();//Используем драйвер
            Connection connection = DriverManager.getConnection(connectionBD.getURL(), connectionBD.getUSER_BD(),
                    connectionBD.getPASSWORD_BD());//Создаем обьект подключения
            Statement statement = connection.createStatement();//Обьект для запроса

            statement.executeUpdate("INSERT INTO tasks " +
                    "(status, name, dateCreate, dateFinish)" +
                    "values(" + task.isStatus() + ", '" + task.getName() + "', '" + task.getDateCreate() + "', '" + task.getDateFinish() + "')");

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        setNewTaskTextField.clear();// Очистка текстового поля после добавления задачи

    }

    //ПОКАЗАТЬ СЕГОДНЯШНИЕ ЗАДАЧИ
    @FXML
    void showTodayTasksClick(ActionEvent event) {
        tasksList.clear(); //Очищаю данные перед показом
        initTodayTasks();
        timePeriodTypeTasks = "todayType";
    }

    //ПОКАЗАТЬ ЗАДАЧИ НА НЕДЕЛЮ
    @FXML
    void showWeekTasksClick(ActionEvent event) {
        tasksList.clear(); //Очищаю данные перед показом
        initWeekTask();
        timePeriodTypeTasks = "weekType";
    }

    //ПОКАЗАТЬ ПОЗДНИЕ ЗАДАЧИ
    @FXML
    void showLaterTaskClick(ActionEvent event) {
        tasksList.clear(); //Очищаю данные перед показом
        initLaterTask();
        timePeriodTypeTasks = "laterType";
    }

    @FXML
    void showAllTasksClick(ActionEvent event) {
        tasksList.clear();
        initAllTask();
        timePeriodTypeTasks = "allType";
    }

    //ОТБОР И ИНИЦИАЛИЗАЦИЯ СЕГОДНЯШНИХ ЗАДАЧ
    private void initTodayTasks() {

        for (Task task : getAllTasksFromBDTasks()
        ) {
            //Распарсил дату и передал в календарь
            DateStringParser prsr = new DateStringParser(task.getDateFinish());
            //Перевожу в тип календарь завершения дату задачи.
            Calendar calendarTask = new GregorianCalendar(prsr.getYear(), prsr.getMonth(), prsr.getDay());
            Calendar weekCalendar = new GregorianCalendar();//Получаю сегодняшнее число

            if (weekCalendar.after(calendarTask) && task.isStatus()) { //Показываю все задачи которые входят в ближайшие 7 дней

                tasksList.add(task);//Добавление задач в таблицу
            }

        }
    }

    //ОТБОР И ИНИЦИАЛИЗАЦИЯ НЕДЕЛЬННЫХ ЗАДАЧ
    public void initWeekTask() {

        for (Task task : getAllTasksFromBDTasks()
        ) {
            //Распарсил дату и передал в календарь
            DateStringParser prsr = new DateStringParser(task.getDateFinish());
            //Перевожу в тип календарь завершения дату задачи.
            Calendar calendarTask = new GregorianCalendar(prsr.getYear(), prsr.getMonth(), prsr.getDay());
            Calendar weekCalendar = new GregorianCalendar();//Получаю сегодняшнее число
            weekCalendar.roll(Calendar.DATE, 7);//Добавляю к сегодняшнему числу 7 дней
            if (weekCalendar.after(calendarTask) && task.isStatus()) { //Показываю все задачи которые входят в ближайшие 7 дней
                tasksList.add(task);//Добавление задач в таблицу
            }

        }
    }

    //ОТБОР И ИНИЦИАЛИЗАЦИЯ ПОЗДНИХ ЗАДАЧ
    private void initLaterTask() {

        for (Task task : getAllTasksFromBDTasks()
        ) {
            if (task.isStatus()) {
                tasksList.add(task);
            }
        }
    }

    //ПОКАЖИ ВСЕ АБСЛОЛЮТНО ВСЕ ЗАДАЧИ
    private void initAllTask() {

        for (Task task : getAllTasksFromBDTasks()
        ) {
            tasksList.add(task);
        }
    }

    @FXML
    void editButtonClick(ActionEvent event) throws IOException {
        editButtonMethod();
    }

    public void editButtonMethod(){
        try {
            ConnectionBD connectionBD = new ConnectionBD();//Создали класс конекта
            Class.forName(connectionBD.getDRIVER()).newInstance();//Используем драйвер
            Connection connection = DriverManager.getConnection(connectionBD.getURL(), connectionBD.getUSER_BD(),
                    connectionBD.getPASSWORD_BD());//Создаем обьект подключения
            Statement statement = connection.createStatement();//слас запроса
            ResultSet resultSet = statement.executeQuery("SELECT * FROM tasks where id=" + idTask + ";");
            Task task = new Task();
            while (resultSet.next()) {
                task.setId(resultSet.getInt("id"));
                task.setStatus(resultSet.getBoolean("status"));
                task.setName(resultSet.getString("name"));
                task.setDateFinish(resultSet.getString("dateFinish"));//System.out.println(resultSet.getString("id"));
            }

            //statement.executeUpdate("DELETE FROM `tasks` WHERE `id` = "+idTask+";");

            //ЭКСПЕРЕМЕНТ С НОВЫМ ОКНОМ
            //TextField statusField = new TextField(String.valueOf(task.isStatus()));
            //TextField statusField = new TextField(String.valueOf(task.isStatus()));
            Button setStatusButton = new Button();

            if (task.isStatus()){
                setStatusButton.setText("Завершить");
            } else {
                setStatusButton.setText("Возобновить");
            }

            //setStatusButton.setPrefWidth(50);
            TextField textField = new TextField(task.getName());
            textField.setPrefWidth(355);
            //setNewTaskDataFinishString = setNewTaskDataFinish.getValue().toString();
            //String dataCreate = new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("uuuu-MM-d"));
            //setNewTaskDataFinish.setPromptText(new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("uuuu-MM-d")));
            DatePicker dateField = new DatePicker();
            dateField.setMinWidth(122);
            dateField.setPromptText(task.getDateFinish());
            //dateField.setPromptText(new GregorianCalendar().toZonedDateTime().format(DateTimeFormatter.ofPattern("uuuu-MM-d")));
            dateField.setPrefWidth(70);

            Button ok = new Button("ok");
            FlowPane secondaryLayout = new FlowPane(setStatusButton, textField, dateField, ok);
            Scene secondScene = new Scene(secondaryLayout, 630, 50);
            Stage newWindow = new Stage();
            newWindow.setTitle("Редактировать");
            newWindow.setScene(secondScene);
            newWindow.show();

            Statement statement1 = connection.createStatement();//слас запроса
            //Кнопка завершающая редактирование
            ok.setOnAction(e ->{

                //String string = dateField.getValue().toString();
                //Ну почти не кастыль, не стал думать как обработать отдельно выбор даты, но нужно посмотртеь
                String dateText;
                try {
                    dateText  = dateField.getValue().toString();
                }catch (NullPointerException exception){
                    dateText = "2040-12-12";
                }

                try {
                    statement1.executeUpdate("UPDATE tasks SET name = '"+textField.getText()+"', status = "+task.isStatus()+", dateFinish = '"+dateText+"' WHERE id = "+task.getId());
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
                newWindow.close();
                //НЕ ТУПИ СДЕЛАЙ ПЕРЕЧИСЛЕНИЯ
                if (timePeriodTypeTasks.equals("todayType")) {
                    tasksList.clear();
                    initTodayTasks();

                }
                if (timePeriodTypeTasks.equals("weekType")) {
                    tasksList.clear();
                    initWeekTask();
                }
                if (timePeriodTypeTasks.equals("laterType")) {
                    tasksList.clear();
                    initLaterTask();
                }
                if (timePeriodTypeTasks.equals("allType")) {
                    tasksList.clear();
                    initAllTask();
                }
            });

            //КНОПКА ЗАВЕРШЕНИЯ ЗАДАЧИ
            setStatusButton.setOnAction(e -> {

                if(task.isStatus()){
                    task.setStatus(false);
                    setStatusButton.setText("Возобновить");
                    ok.getOnAction();
                } else {
                    task.setStatus(true);
                    setStatusButton.setText("Завершить");
                }
            });
            
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }



    //ПОЛУЧЕНИЕ НОВЫХ ЗАДАЧ ИЗ БАЗЫ И ДОБАВЛЕНЕИ В КОЛЛККЦИЮ
    private ObservableList<Task> getAllTasksFromBDTasks() {
        ObservableList<Task> list = FXCollections.observableArrayList(); //Тут хранятся хадачи

        try {

            ConnectionBD connectionBD = new ConnectionBD();//Создали класс конекта
            Class.forName(connectionBD.getDRIVER()).newInstance();//Используем драйвер
            Connection connection = DriverManager.getConnection(connectionBD.getURL(), connectionBD.getUSER_BD(),
                    connectionBD.getPASSWORD_BD());//Создаем обьект подключения
            Statement statement = connection.createStatement();//слас запроса
            ResultSet resultSet = statement.executeQuery("SELECT * FROM tasks");//Запрос
            while (resultSet.next()) { //Перебор результата
                Integer id = Integer.parseInt(resultSet.getString("id"));

                Boolean status;
                if (resultSet.getString("status").equals("1")) {
                    status = true;
                } else {
                    status = false;
                }
                String name = resultSet.getString("name");
                String dateCreate = (resultSet.getString("dateCreate"));
                String dateFinish = (resultSet.getString("dateFinish"));

                list.add(new Task(id, status, name, dateCreate, dateFinish));
            }

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return list;
    }

    //УДАЛИТЬ ЗАДАЧУ ПО ИНДЕКСУ
    @FXML
    void deleteButtonClick(ActionEvent event) {
        try {
            ConnectionBD connectionBD = new ConnectionBD();//Создали класс конекта
            Class.forName(connectionBD.getDRIVER()).newInstance();//Используем драйвер
            Connection connection = DriverManager.getConnection(connectionBD.getURL(), connectionBD.getUSER_BD(),
                    connectionBD.getPASSWORD_BD());//Создаем обьект подключения
            Statement statement = connection.createStatement();//слас запроса

            statement.executeUpdate("DELETE FROM `tasks` WHERE `id` = " + idTask + ";");

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        //НЕ ТУПИ СДЕЛАЙ ПЕРЕЧИСЛЕНИЯ
        if (timePeriodTypeTasks.equals("todayType")) {
            tasksList.clear();
            initTodayTasks();

        }
        if (timePeriodTypeTasks.equals("weekType")) {
            tasksList.clear();
            initWeekTask();
        }
        if (timePeriodTypeTasks.equals("laterType")) {
            tasksList.clear();
            initLaterTask();
        }
        if (timePeriodTypeTasks.equals("allType")) {
            tasksList.clear();
            initAllTask();
        }

    }
}


