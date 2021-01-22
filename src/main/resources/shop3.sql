CREATE TABLE sale (
	saleid int PRIMARY KEY,
	userid varchar(10) NOT NULL,
	saledate datetime
);
CREATE TABLE saleitem (
	saleid int ,
	seq int ,
	itemid int NOT NULL,
	quantity int,
	PRIMARY KEY (saleid, seq)
);