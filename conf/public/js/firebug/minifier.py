import os,sys,re,jsmin

def minify(_str):
	return jsmin.jsmin(_str)

if __name__ == "__main__":
	path = sys.argv[1]
	target = sys.argv[2]
	source = open(path).read()
	print 'The file has been written to: %s'%(target)
	f = open(target,'w')
	f.write(minify(source))
	f.close()