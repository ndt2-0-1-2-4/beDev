	DROP DATABASE IF EXISTS doan;
	create schema doan;
	use doan;
	create table users(
		id int primary key auto_increment ,
		tk char(40),
		mk char(40),
		fullname char(50),
		email char(50),
        role char(10),
        is_delete BOOLEAN DEFAULT FALSE,
		is_active BOOLEAN DEFAULT TRUE
	);
	insert into users(tk,mk,fullname,email,role) values 
	('thanhdz','1','Phạm Như Thành','thanhit482004@gmail.com','user'),
	('thanhcute','1','Nguyễn Văn Thiệu','thieucho@gmail.com','user'),
	('thanh','1','Hạ Hầu Đôn','downmonkey@gmail.com','user'),
	('thanh2004','1','Nguyễn Đình Hưng','thanhcute@gmail.com','user'),
    ('thanh2006','1','Nguyễn Đình h','thanhcute1@gmail.com','user'),
    ('thanh2005','1','Nguyễn Đình c','thanhcute2@gmail.com','user'),
	('admin','admin','Admin','admin@gmail.com','ADMIN');
	update users set id =0 where tk= 'admin';

	create table listgame(
		id int primary key auto_increment ,
		namegame char(40) 
	);
	insert into listgame(namegame) values 
	('Đoán số '),
	('Bầu cua'),
	('Vòng quay may mắn'),
	('Slot 777');

	create table sessionplayer(
		id int primary key auto_increment,
		namegame char(40) ,
		playerid int ,
		timeoccurs datetime,
		result char(20),
		bet float ,
		reward float,
		choice char(20),
		foreign key (playerid) references users(id)
	);
	#insert into sessionplayer(gameid,playerid ,timeoccurs,result ,pointbet,pointrc,bet)values();

	create table sessiongame (
		 id int primary key auto_increment,
	--      idgame int ,
		 namegame char(40),
		 result char(10),
		 timeoccurs char(50) 
	--      foreign key (idgame) references listgame(id)
	);
	#insert into sessiongame(idgame,namegame,result,timeoccurs)values(1,'Đoán số ','L','2024-10-25 01:06:45');

	create table atm(
		idplayer int ,
		stk char(40) unique,
		balance float ,
		foreign key (idplayer) references users(id)
	);
	insert into atm values
    (0,'1',100000000000),
	(1,'0787107821',5000),
	(2,'04082004',5000),
	(3,'30041975',5000),
	(4,'66668888363636',5000);

	create table historybalance(
		id int primary key auto_increment ,
		id_player int ,
		timechange datetime,
		content text,
		trans float ,
		balance float ,
		foreign key (id_player) references users(id)
	);
	#insert into historybalance value()

	-- create table mission(
	--  id int primary key auto_increment,
	--  request float ,
	--  award float ,
	--  statu text 
	-- );
	-- insert into mission(request,award,statu) values
	-- (1000,100),
	-- (5000,500),
	-- (20000,1000),
	-- (50000,2000),
	-- (100000,5000),
	-- (200000,10000);

	create table recive(
		id int primary key auto_increment,
		idplayer int ,
		request float ,
		daterecive date
	);

	create table store(
		percent int primary key ,
		price int ,
		img text
	);
	insert into store value 
	(25,25000,'/img'),
	(50,75000,'/img'),
	(75,150000,'/img'),
	(90,250000,'/img'),
	(100,1000000,'/img');

	create table historyBuy (
		id int primary key auto_increment,
		idstore int ,
		datebuy date
	);

	create table friend(
		id int primary key auto_increment,
		id_my int ,
		id_friend int ,
		relative char(50),
		foreign key (id_my) references users(id),
		foreign key (id_friend) references users(id)
	);
	create table message(
		id int primary key auto_increment,
		id_my int ,
		id_friend int ,
		content char(50),
		time_send char(40),
		status tinyint default 0 ,
		foreign key (id_my) references users(id),
		foreign key (id_friend) references users(id)
	);
    
    create table betHisfbxs(
		id int primary key auto_increment,
        idplayer int ,
        bet_type ENUM('FOOTBALL', 'LOTTERY') NOT NULL,
		reference_id varchar(255) NOT NULL,  -- id tham chieu (fb, xs)
		prediction VARCHAR(255), -- du doan
		bet_amount INT NOT NULL,
		bet_time DATETIME DEFAULT CURRENT_TIMESTAMP,  -- thoi gian cuoc
		multi int,
        foreign key (idplayer) references users(id) ,
		`status` BIT DEFAULT 0
    );


   