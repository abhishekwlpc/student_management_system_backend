create table users(
	user_id int primary key auto_increment,
    username varchar(100) unique not null,
    email varchar(255) unique not null,
    password_hash varchar(255) not null,
    role VARCHAR(20) not null,
    must_change_password boolean not null default true
);

create table students(
	student_id int primary key auto_increment,
    user_id int unique not null,
    registration_number varchar(50) unique not null,
    first_name varchar(255) not null,
    last_name varchar(255) not null,
    birth_date date not null,
    address varchar(255) not null,
    guardian_name varchar(255),
    guardian_relationship varchar(255),
    guardian_telephone varchar(30),
    
    foreign key (user_id)
    references users(user_id)
);
