package sample.sourse;

import javafx.scene.control.Button;
import sample.controller.Controller;

import java.util.Formattable;

public class Task {
    private int id;
    private Button setStatus;
    private boolean status;
    private String statusString;
    private String name;
    private String dateCreate;
    private String dateFinish;
    private Button edit;
    private Button del;

    public  Task() {

    }


    public Task(boolean status, String name, String dateCreate, String dateFinish) {
        this.setStatus = new Button("o");
        this.status = status;
        this.name = name;
        this.dateCreate = dateCreate;
        this.dateFinish = dateFinish;
        this.edit = new Button("edit");
        this.del = new Button("del");
        setStatus.setOnAction(e -> {
            System.out.println("Огонь бля!");
        });
    }

    public Task(int id, boolean status, String name, String dateCreate, String dateFinish) {
        //this.setStatus = new Button("o9");
        this.id = id;
        this.status = status;
        this.name = name;
        this.dateCreate = dateCreate;
        this.dateFinish = dateFinish;
        this.edit = new Button("edit");
        this.del = new Button("del");

        //ЗАМЕНИ ЭТО ГОВНО ВМЕНЯЕМЫМ КОДОМ
        //Кнопка отрисовки статусза задачи true активна false завершена
        if(this.status){
            this.setStatus = new Button("true ");

        }else if (!this.status){
            this.setStatus = new Button("false");
        }
        //Меняем статус задачи
        //Тут будет запрос меняющий статус
        setStatus.setOnAction(e -> {
            //System.out.println("Огонь бля!");
            this.status = false;
            Controller controller = new Controller();
            controller.tasksList.clear();
            //controller.initWeekTask();
            System.out.println("Огонь бля!");
        });

    }

    public Button getSetStatus() {
        return setStatus;
    }

    public void setSetStatus(Button setStatus) {
        this.setStatus = setStatus;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateCreate() {
        return dateCreate;
    }

    public void setDateCreate(String dateCreate) {
        this.dateCreate = dateCreate;
    }

    public String getDateFinish() { return dateFinish; }

    public void setDateFinish(String dateFinish) {
        this.dateFinish = dateFinish;
    }

    public Button getEdit() { return edit; }

    public void setEdit(Button edit) { this.edit = edit; }

    public Button getDel() { return del; }

    public void setDel(Button del) { this.del = del; }
}
